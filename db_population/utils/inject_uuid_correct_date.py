import json
import random
import sys
from datetime import datetime, timedelta


def generate_random_date():
    end = datetime.now()
    start = end - timedelta(days=365)
    return start + (end - start) * random.random()


def format_as_isodate(date):
    return {"$date": date.strftime("%Y-%m-%dT%H:%M:%S.%fZ")}


def generate_uuid_values_list(companies_file_path):
    uuid_list = []
    with open(companies_file_path, "r") as f:
        data = json.load(f)
    companies = data["APsystem.company"]

    for element in companies:
        uuid = element["_id"]["$binary"]["base64"]
        uuid_list.append(uuid)

    return uuid_list


def inject_value(element, value_pair, collection_name):
    if collection_name == "purchaseOrder":
        element["buyer"]["$binary"]["base64"] = value_pair[0]
        element["seller"]["$binary"]["base64"] = value_pair[1]
    if collection_name == "invoice":
        element["buyerId"]["$binary"]["base64"] = value_pair[0]
        element["sellerId"]["$binary"]["base64"] = value_pair[1]

    return element


def inject_history_array(element, collection_name, statuses_count):
    if collection_name == "purchaseOrder":
        statuses_state = random.randint(1, 2)
        if statuses_state % 2:
            statuses = ["CREATED", "SAVED", "REJECTED"]
        else:
            statuses = ["CREATED", "SAVED", "APPROVED"]

    if collection_name == "invoice":
        statuses = ["CREATED", "SENT", "PAID"]

    # Just ensuring that the specified statuses_count is within the valid range
    statuses_count = min(statuses_count, len(statuses))

    dates = sorted([generate_random_date() for _ in range(statuses_count)])
    status_history = [{"date": format_as_isodate(date), "status": statuses[i]} for i, date in enumerate(dates)]
    element['statusHistory'] = status_history
    return element

def compute_total_amount(element):
    return sum(int(item['quantity']['$numberInt']) * float(item['price']['$numberDouble']) for item in element['items'])

if __name__ == "__main__":

    companies_file_path = sys.argv[1]
    collection_file_path = sys.argv[2]
    collection_name = sys.argv[3]
    output_file_path = sys.argv[4]
    status_distribution = int(sys.argv[5])  # 0 --> 100% UNPAID, 1 --> 100% PAID, 2 --> RANDOM, 3 --> 50% PAID

    uuid_list = generate_uuid_values_list(companies_file_path)

    with open(collection_file_path, "r") as f:
        data = json.load(f)

    last_invoice_per_buyer = {}

    # print(data[f"APsystem.{collection_name}"])
    for i, element in enumerate(data[f"APsystem.{collection_name}"]):
        lhs_uuid = random.choice(uuid_list)
        rhs_uuid = random.choice(uuid_list)
        random_pair = (lhs_uuid, rhs_uuid)

        element = inject_value(element, random_pair, collection_name)

        if collection_name == 'invoice':
            buyer_id = element["buyerId"]["$binary"]["base64"]
            if buyer_id not in last_invoice_per_buyer:
                last_invoice_per_buyer[buyer_id] = None

            if status_distribution == 0:  # 0 --> 100% UNPAID
                unpaid_statuses_count = random.randint(1, 2)
                element = inject_history_array(element, collection_name, statuses_count=unpaid_statuses_count)
            elif status_distribution == 1:  # 1 --> 100% PAID
                element = inject_history_array(element, collection_name, statuses_count=3)
            elif status_distribution == 2:  # 2 --> RANDOM
                random_statuses_count = random.randint(1, 3)
                element = inject_history_array(element, collection_name, statuses_count=random_statuses_count)
            elif status_distribution == 3:  # 3 --> 50% PAID
                if last_invoice_per_buyer[buyer_id] and last_invoice_per_buyer[buyer_id]['statusHistory'][-1]['status'] == 'PAID':
                    statuses_count = random.randint(1, 2)  # Alternating with 'UNPAID'
                else:
                    statuses_count = 3  # 'PAID'

                element = inject_history_array(element, collection_name, statuses_count=statuses_count)

            element['totalAmount']['$numberDouble'] = f"{compute_total_amount(element)}"
            last_invoice_per_buyer[buyer_id] = element
        else:
            element = inject_history_array(element, collection_name, statuses_count=2)

    with open(output_file_path, "w") as f:
        json.dump(data[f"APsystem.{collection_name}"], f)
