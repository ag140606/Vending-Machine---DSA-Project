# Vending-Machine : DSA-Project
💡 Smart Vending Machine using Bloom Filters and Red-Black Trees

Modern vending machines handle large inventories and need to process product availability queries quickly. To enhance efficiency, a Smart Vending Machine Management System that uses Bloom Filters for fast probabilistic product existence checks and Red-Black Trees (RB Trees) for efficient product storage, retrieval, and management has been designed.

⸻

🕹️ System Overview

The vending machine maintains a database of products (e.g., snacks, beverages, etc.), each having:  

	1. Product Name (string)
	2. Price (int)
	3. Stock (int)

⸻

⚙️ Data Structures Used

	1.	Bloom Filter – For quick “is this product possibly in stock?” queries before performing a full search.
	2.	Red-Black Tree – To maintain product records in sorted order (by Product ID or Name) and allow efficient insert, delete, and search operations.

⸻

🎯 Functional Requirements  

	1.	Insert Product
	2.	Search Product
	3.	Purchase Product
	5.	Display Inventory
	6.	Check Product Availability
  	7.  Continue according to balance available
