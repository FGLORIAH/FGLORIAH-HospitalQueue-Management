# ACS 311 Week 5: Hospital Queue Management System (Python version)
# Patients get a queue number and are served by priority:
# EMERGENCY first, then PRIORITY (children 5 and under, elderly 65+), then NORMAL.

AVG_MINUTES = 8.5  # average consultation time per patient (float)

queue = []  # list of dictionaries, one per waiting patient
counters = {"EMERGENCY": 0, "PRIORITY": 0, "NORMAL": 0}
PREFIX = {"EMERGENCY": "E", "PRIORITY": "P", "NORMAL": "N"}
RANK = {"EMERGENCY": 0, "PRIORITY": 1, "NORMAL": 2}
arrival_counter = 0
served_count = 0


def get_category(age, is_emergency):
    """Decide the patient category from age and emergency flag."""
    if is_emergency:
        return "EMERGENCY"
    if age <= 5 or age >= 65:
        return "PRIORITY"
    return "NORMAL"


def sort_queue():
    """Order the queue by category rank, then by arrival order."""
    queue.sort(key=lambda p: (RANK[p["category"]], p["arrival"]))


def estimate_wait(position):
    """Estimated waiting time in minutes for a patient at this position."""
    return position * AVG_MINUTES


def read_age():
    """Keep asking until the user types a valid age (0 to 120)."""
    while True:
        text = input("Enter patient age: ").strip()
        try:
            age = int(text)
        except ValueError:
            print("Invalid input: age must be a whole number. Try again.")
            continue
        if age < 0 or age > 120:
            print("Invalid input: age must be between 0 and 120. Try again.")
            continue
        return age


def register_patient():
    """Register a new patient and give them a queue number."""
    global arrival_counter
    name = input("Enter patient name: ").strip()
    if name == "":
        print("Invalid input: name cannot be empty.")
        return
    age = read_age()
    answer = input("Is this an emergency? (y/n): ").strip().lower()
    is_emergency = answer == "y"

    category = get_category(age, is_emergency)
    counters[category] += 1
    arrival_counter += 1
    number = PREFIX[category] + "-" + str(counters[category]).zfill(3)

    queue.append({"number": number, "name": name, "age": age,
                  "category": category, "arrival": arrival_counter})
    sort_queue()
    position = [p["number"] for p in queue].index(number)
    print(f"Registered: {name} | Queue number: {number} | Category: {category}")
    print(f"Patients ahead: {position} | Estimated wait: {estimate_wait(position):.1f} minutes")


def call_next():
    """Call the next patient and remove them from the queue."""
    global served_count
    if len(queue) == 0:
        print("The queue is empty. No patient to call.")
        return
    patient = queue.pop(0)
    served_count += 1
    print(f"Now calling: {patient['number']} - {patient['name']} ({patient['category']})")


def display_queue():
    """Show every waiting patient in service order."""
    if len(queue) == 0:
        print("The queue is empty.")
        return
    print(f"{'Pos':<5}{'Number':<9}{'Name':<15}{'Age':<6}{'Category':<11}{'Wait (min)'}")
    for i, p in enumerate(queue):
        print(f"{i + 1:<5}{p['number']:<9}{p['name']:<15}{p['age']:<6}{p['category']:<11}{estimate_wait(i):.1f}")


def show_summary():
    """Show totals for the day."""
    print(f"Served so far: {served_count} | Still waiting: {len(queue)}")
    for category in ("EMERGENCY", "PRIORITY", "NORMAL"):
        waiting = sum(1 for p in queue if p["category"] == category)
        print(f"  {category}: registered {counters[category]}, waiting {waiting}")


def main():
    print("=== Hospital Queue Management System ===")
    while True:
        print("\n1. Register patient\n2. Call next patient\n3. View queue\n4. Summary\n5. Exit")
        choice = input("Choose an option (1-5): ").strip()
        if choice == "1":
            register_patient()
        elif choice == "2":
            call_next()
        elif choice == "3":
            display_queue()
        elif choice == "4":
            show_summary()
        elif choice == "5":
            print("Goodbye.")
            break
        else:
            print("Invalid option. Please enter a number from 1 to 5.")


main()
