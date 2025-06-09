# Customer_Realtionship_Management-System
#This is a Java-based CRM (Customer Relationship Management) desktop application built using Java Swing for the user interface and MySQL for data storage. The system allows administrators and sales staff to manage users, customers, sales orders, and transactions effectively.





Features
🔐 User login system with role-based access (Admin, Sales, Manager)

👤 Manage customers and salespeople (with photo & document uploads)

🧾 Track orders and transactions

📊 View login history and activity logs

💾 Uses MySQL as backend database

🎨 Clean, modern UI using custom themes and colors






Tech Stack
Java Swing (UI)

MySQL (Database)

JDBC (Database connectivity)

JDK 22+

IntelliJ IDEA (Development environment)




🖥️ How to Run


1.Clone the repository:

git clone https://github.com/yourusername/CRM_System.git

2.Import into IntelliJ IDEA or any Java IDE.

3.Make sure MySQL is running and database is created using the provided SQL script.


🛠️ Database Setup

Open MySQL Workbench or any MySQL client.

Run the crm.sql script from this repo to create tables and sample data.


📁 Folder Structure

CRM_System/

├── src/
│   └── CRMLogin.java

│   └── CRMDashboard.java

│   └── DatabaseManager.java

│   └── TestDBConnection.java

│   └── Salesman.java

│   └── Customer.java

│   └── Order.java

│   └── Transaction.java

├── lib/

│   └── mysql-connector-j-9.2.0.jar

├── crm.sql




4.Run CRMLogin.java to start the application.





