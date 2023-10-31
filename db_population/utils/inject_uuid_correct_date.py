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


def inject_value(element,value_pair,collection_name):
	if collection_name == "purchaseOrder":
		element["buyer"]["$binary"]["base64"] = value_pair[0]
		element["seller"]["$binary"]["base64"] = value_pair[1]
	if collection_name == "invoice":
		element["buyerId"]["$binary"]["base64"] = value_pair[0]
		element["sellerId"]["$binary"]["base64"] = value_pair[1]
	
	return element
	
def inject_history_array(element,collection_name):
	
	if collection_name == "purchaseOrder":
		statuses_state = random.randint(1,2)
		if statuses_state % 2:
			statuses = ["CREATED", "SAVED", "REJECTED"]
		else:
			statuses = ["CREATED", "SAVED", "APPROVED"]
			
	if collection_name == "invoice":
		statuses = ["CREATED", "SENT", "PAID"]

	
	dates = sorted([generate_random_date() for _ in range(random.randint(1, len(statuses)))])
	order_history = [{"date": format_as_isodate(date), "status": statuses[i]} for i, date in enumerate(dates)]
	element['orderHistory'] = order_history
	return element

	
if __name__=="__main__":
	
	companies_file_path = sys.argv[1]
	collection_file_path = sys.argv[2]
	collection_name = sys.argv[3]
	output_file_path = sys.argv[4]
	
			
	uuid_list = generate_uuid_values_list(companies_file_path)
	
	with open(collection_file_path, "r") as f:
		data = json.load(f)
	
	for element in data[f"APsystem.{collection_name}"]:
			
		lhs_uuid = random.choice(uuid_list)
		rhs_uuid = random.choice(uuid_list)

		random_pair = (lhs_uuid,rhs_uuid)
		element = inject_value(element,random_pair,collection_name)
		element = inject_history_array(element,collection_name)
	
	with open(output_file_path,"w") as f:
		json.dump(data[f"APsystem.{collection_name}"],f)

