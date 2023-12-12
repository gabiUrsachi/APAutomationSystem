import re
import matplotlib.pyplot as plt
from collections import defaultdict
import math

with open('time-analytics.log', 'r') as file:
    log_content = file.read()

status_pattern = re.compile(r"STATUS DISTRIBUTION: (\d+) -> (.+?) invoices PAID")
index_pattern = re.compile(r"WITH INDEXES|WITHOUT INDEXES")
operation_pattern = re.compile(r"\[Operation: (.+?)\]")
time_pattern = re.compile(r"Execution time: (\d+) ms")
targeted_operations = [
    "InvoiceCustomRepository.findByFiltersPageable(..)",
    "InvoiceCustomRepository.getPaidAmountForLastNMonths(..)",
    "InvoiceCustomRepository.findLastMonthPaidInvoicesByBuyerUUIDAndSellerUUID(..)"
]

data = defaultdict(lambda: defaultdict(lambda: defaultdict(list)))

current_status = None
current_index = None
current_operation = None
first_appearance = defaultdict(lambda: defaultdict(bool))

for line in log_content.split("\n"):
    status_match = status_pattern.match(line)
    index_match = index_pattern.search(line)
    time_match = time_pattern.search(line)
    operation_match = operation_pattern.search(line)

    if status_match:
        current_status = status_match.group(1)
    elif index_match:
        current_index = "INDEXES" if "WITH INDEXES" in line else "NO INDEXES"
    elif operation_match and time_match and current_status and current_index:
        current_operation = operation_match.group(1)
        if current_operation in targeted_operations:
            if not first_appearance[current_status][current_operation]:
                first_appearance[current_status][current_operation] = True
            else:
                data[current_status][current_index][current_operation].append(int(time_match.group(1)))

averages = defaultdict(lambda: defaultdict(lambda: defaultdict(float)))

for status, indexes_data in data.items():
    for index, operations_data in indexes_data.items():
        for operation, times in operations_data.items():
            averages[status][operation][index] = sum(times) / len(times)

# plot
statuses = sorted(data.keys())
indexes = ["INDEXES", "NO INDEXES"]
bar_width = 0.2
index_positions = range(len(indexes))

for i, status in enumerate(statuses):
    num_operations = len(averages[status])
    num_rows = num_operations
    num_columns = math.ceil(num_operations / 3)

    plt.figure(figsize=(9, 2 * num_rows))
    plt.suptitle(f'Status distribution: {status}', fontsize=10)

    for j, (operation, avg_data) in enumerate(averages[status].items()):
        ax = plt.subplot(num_rows, num_columns, j + 1)
        bars = ax.bar(index_positions, [avg_data[index] for index in indexes], bar_width, label=operation)
        # plt.xlabel('Index Usage')
        plt.ylabel('Average Execution Time (ms)')
        plt.xticks(index_positions, indexes)
        plt.legend()

        for idx, bar in enumerate(bars):
            height = bar.get_height()
            ax.text(bar.get_x() + bar.get_width() / 2, 0, f'{height:.2f}', ha='center', va='bottom', color='red', fontsize=10)

        ax.set_ylim(0, max([bar.get_height() for bar in bars]) * 1.4)

    plt.tight_layout(rect=[0, 0.03, 1, 0.95])
    plt.show()