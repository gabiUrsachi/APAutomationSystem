package org.example.integration;

import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.utils.CompanyRole;
import org.example.persistence.utils.InvoiceStatus;
import org.example.persistence.utils.data.InvoiceFilter;
import org.example.persistence.utils.data.InvoiceStatusHistoryObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@SpringBootTest(classes = {org.example.Main.class})
@ActiveProfiles(profiles = {"test"})
public class InvoiceRepositoryShould {

    @Autowired
    InvoiceRepository invoiceRepository;

    private final UUID BUYER_ID = UUID.randomUUID();
    private final UUID SELLER_ID = UUID.randomUUID();
    private final Random RANDOM = new Random();
    private List<Invoice> invoices;

    @BeforeEach
    public void initialize() {
        this.invoices = new ArrayList<>();
    }

    @AfterEach
    public void clean() {
        invoices.forEach(invoice -> this.invoiceRepository.deleteByIdentifier(invoice.getIdentifier()));
    }

    @Test
    public void return0PaidAmountOverSomeMonthsIfNoPaidInvoicesExists() {
        // given
        List<Invoice> unpaidInvoices = generateInvoices(InvoiceStatus.SENT, randomValue(), null, null);

        this.invoices.addAll(unpaidInvoices);
        this.invoiceRepository.saveAll(invoices);

        // when
        Float computedPaidAmountForLast3Months = this.invoiceRepository.getPaidAmountForLastNMonths(BUYER_ID, SELLER_ID, 3);

        // then
        Assertions.assertEquals(0f, computedPaidAmountForLast3Months);
    }

    @Test
    public void correctlyComputePaidAmountOverSomeMonths() {
        // given
        Float oneMonthAgoPaidAmount = 10000f;
        List<Invoice> oneMonthAgoPaidInvoices = generateInvoices(InvoiceStatus.PAID, randomValue(), oneMonthAgoPaidAmount, 1);

        Float twoMonthsAgoPaidAmount = 300f;
        List<Invoice> twoMonthsAgoPaidInvoices = generateInvoices(InvoiceStatus.PAID, randomValue(), twoMonthsAgoPaidAmount, 2);

        Float fourMonthsAgoPaidAmount = 23500f;
        List<Invoice> fourMonthsAgoPaidInvoices = generateInvoices(InvoiceStatus.PAID, randomValue(), fourMonthsAgoPaidAmount, 4);

        List<Invoice> unpaidInvoices = generateInvoices(InvoiceStatus.SENT, RANDOM.nextInt(10) + 1, null, null);

        this.invoices.addAll(oneMonthAgoPaidInvoices);
        this.invoices.addAll(twoMonthsAgoPaidInvoices);
        this.invoices.addAll(fourMonthsAgoPaidInvoices);
        this.invoices.addAll(unpaidInvoices);

        this.invoiceRepository.saveAll(invoices);

        // when
        Float computedPaidAmountForLast3Months = this.invoiceRepository.getPaidAmountForLastNMonths(BUYER_ID, SELLER_ID, 3);

        // then
        Float expectedPaidAmountForLast3Months = oneMonthAgoPaidAmount + twoMonthsAgoPaidAmount;
        Assertions.assertEquals(expectedPaidAmountForLast3Months, computedPaidAmountForLast3Months);
    }

    @Test
    public void properlySearchForInvoicesThatMatchCriteria() {
        // given
        List<Invoice> paidInvoices = generateInvoices(InvoiceStatus.PAID, randomValue(), null, null);
        List<Invoice> sentInvoices = generateInvoices(InvoiceStatus.SENT, randomValue(), null, null);
        List<Invoice> createdInvoices = generateInvoices(InvoiceStatus.CREATED, randomValue(), null, null);

        this.invoices.addAll(paidInvoices);
        this.invoices.addAll(sentInvoices);
        this.invoices.addAll(createdInvoices);

        this.invoiceRepository.saveAll(invoices);

        List<InvoiceFilter> invoiceFilters = List.of(
                InvoiceFilter.builder().requiredStatus(InvoiceStatus.PAID).companyType(CompanyRole.BUYER).companyUUID(BUYER_ID).build(),
                InvoiceFilter.builder().requiredStatus(InvoiceStatus.SENT).companyType(CompanyRole.BUYER).companyUUID(BUYER_ID).build()
        );

        // when
        List<Invoice> foundInvoices = this.invoiceRepository.findByFilters(invoiceFilters);

        // then
        Integer foundInvoicesCount = foundInvoices.size();
        Integer searchedInvoicesCount = paidInvoices.size() + sentInvoices.size();

        List<Invoice> searchedInvoices = new ArrayList<>(paidInvoices);
        searchedInvoices.addAll(sentInvoices);

        List<UUID> foundInvoicesIdentifiers = foundInvoices.stream().map(Invoice::getIdentifier).collect(Collectors.toList());
        List<UUID> searchedInvoicesIdentifiers = searchedInvoices.stream().map(Invoice::getIdentifier).collect(Collectors.toList());

        Assertions.assertEquals(searchedInvoicesCount, foundInvoicesCount);
        Assertions.assertTrue(searchedInvoicesIdentifiers.containsAll(foundInvoicesIdentifiers));
        Assertions.assertTrue(foundInvoicesIdentifiers.containsAll(searchedInvoicesIdentifiers));
    }

    @Test
    public void returnAllExistingInvoicesIfNoFiltersRequired() {
        // given
        List<Invoice> paidInvoices = generateInvoices(InvoiceStatus.PAID, randomValue(), null, null);
        List<Invoice> sentInvoices = generateInvoices(InvoiceStatus.SENT, randomValue(), null, null);
        List<Invoice> createdInvoices = generateInvoices(InvoiceStatus.CREATED, randomValue(), null, null);

        this.invoices.addAll(paidInvoices);
        this.invoices.addAll(sentInvoices);
        this.invoices.addAll(createdInvoices);

        this.invoiceRepository.saveAll(invoices);

        // when
        List<Invoice> foundInvoices = this.invoiceRepository.findByFilters(null);

        // then
        Integer foundInvoicesCount = foundInvoices.size();
        Integer searchedInvoicesCount = paidInvoices.size() + sentInvoices.size() + createdInvoices.size();

        Assertions.assertEquals(searchedInvoicesCount, foundInvoicesCount);
    }

    @Test
    public void returnPagedInvoicesAccordingToTheGivenPagingParams() {
        // given
        List<Invoice> paidInvoices = generateInvoices(InvoiceStatus.PAID, randomValue(), null, null);
        List<Invoice> sentInvoices = generateInvoices(InvoiceStatus.SENT, randomValue(), null, null);
        List<Invoice> createdInvoices = generateInvoices(InvoiceStatus.CREATED, randomValue(), null, null);

        this.invoices.addAll(paidInvoices);
        this.invoices.addAll(sentInvoices);
        this.invoices.addAll(createdInvoices);

        this.invoiceRepository.saveAll(invoices);

        // when
        int page = 0;
        int pageSize = invoices.size()/2;

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Invoice> foundInvoices = this.invoiceRepository.findByFiltersPageable(null, pageable);

        // then
        Integer foundInvoicesCount = foundInvoices.getNumberOfElements();
        Integer searchedInvoicesCount = pageSize;

        Assertions.assertEquals(searchedInvoicesCount, foundInvoicesCount);
    }

    private List<Invoice> generateInvoices(InvoiceStatus invoicesStatus, Integer invoicesCount, Float totalAmount, Integer monthsPassedFromStatusChange) {
        List<Invoice> invoices = new ArrayList<>();
        List<Float> amountsPerInvoices = generateRandomAmounts(totalAmount, invoicesCount);

        for (int i = 0; i < invoicesCount; i++) {
            Float invoiceAmount = amountsPerInvoices.get(i);
            List<InvoiceStatusHistoryObject> invoiceStatusHistory = createStatusHistory(invoicesStatus, monthsPassedFromStatusChange);

            Invoice invoice = createCustomInvoice(invoiceStatusHistory, invoiceAmount);

            invoices.add(invoice);
        }

        return invoices;
    }

    private List<Float> generateRandomAmounts(Float totalAmount, Integer amountsCount) {
        Float[] amounts = new Float[amountsCount];
        Arrays.fill(amounts, 0f);

        if(totalAmount != null){
            for (int i = 0; i < totalAmount; i++) {
                amounts[(int) (Math.random() * amountsCount)]++;
            }
        }

        return List.of(amounts);
    }

    private List<InvoiceStatusHistoryObject> createStatusHistory(InvoiceStatus lastStatus, Integer monthsPassedFromStatusChange) {
        List<InvoiceStatusHistoryObject> statusHistory = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        int lastStatusOrdinal = lastStatus.ordinal();

        for (int i = 0; i <= lastStatusOrdinal; i++) {
            InvoiceStatus currentStatus = InvoiceStatus.values()[i];
            LocalDateTime currentStatusDate = currentTime.minusMonths(Objects.requireNonNullElseGet(monthsPassedFromStatusChange, () -> RANDOM.nextInt(100)));

            InvoiceStatusHistoryObject invoiceStatusHistoryObject = createStatusHistoryObject(currentStatus, currentStatusDate);

            statusHistory.add(invoiceStatusHistoryObject);
        }

        return statusHistory;
    }

    private InvoiceStatusHistoryObject createStatusHistoryObject(InvoiceStatus invoiceStatus, LocalDateTime invoiceStatusDate){

        return InvoiceStatusHistoryObject.builder()
                .status(invoiceStatus)
                .date(invoiceStatusDate)
                .build();
    }

    private Invoice createCustomInvoice(List<InvoiceStatusHistoryObject> statusHistory, Float totalAmount) {

        return Invoice.builder()
                .identifier(UUID.randomUUID())
                .buyerId(BUYER_ID)
                .sellerId(SELLER_ID)
                .version(0)
                .statusHistory(statusHistory)
                .totalAmount(totalAmount)
                .build();
    }

    private Integer randomValue(){
        return RANDOM.nextInt(10) + 1;
    }
}