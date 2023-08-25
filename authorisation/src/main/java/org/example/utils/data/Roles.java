package org.example.utils.data;

public enum Roles {
    ADMIN, // create users with different roles
    BUYER_CUSTOMER, // view/create/send/edit PO
    BUYER_FINANCE,  // view/pay INVOICE
    SUPPLIER_ACCOUNTING, // view/create/edit/send INVOICE, view approved PO
    SUPPLIER_MANAGEMENT // approve/reject PO
}
