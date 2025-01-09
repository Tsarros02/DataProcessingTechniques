# DataProcessingTechniques project 
This repository consists of 3 files and a folder:
1. A .py file for scraping data from the booking site
2. A XLSX file that is the result of the python script
3. An IPYNB file that cleans the XLSX file and saves the clean data in a CSV
4. A Main folder that consists of :
   Main/src, Main/pom.xml

In the .py file i use the playwright library to create a semi-manual a browser automation tool that searches in a booking url that contains some specific filters:
* Destination: Athens
* Check-in and Check-out Dates: User-defined dates
* Distance from City Centre: Hotels within 3 km from the city center
* Accommodation: For 1 adult

The script automates the following actions:

* Page navigation: Automatically opens the specified URL with the filters.
* Data extraction: Locates and extracts specific hotel details such as:
  * Hotel name
  * Price
  * Distance from the city center
* Page scrolling: Uses JavaScript to scroll through the page, loading more hotel listings.

*The only part that is not automated is the clicking of the load more button which unfortunatelly must be done by the user manually for the time being*

**The .py file and the resulting data are shared solely for educational purposes. This script is not intended for commercial use, and the data provided is for learning and experimentation only**

In the IPYNB file the data is transformed from the raw data collected through the scraping to data of the format (x,y) where x = price and y = distance from the city centre.
(*For more information refer to the ipynb file*)
