# DataProcessingTechniques project 
This repository consists of 3 files and a folder:
1. A .py file for scraping data from the booking site
2. An XLSX file that is the result of the python script
3. An IPYNB file that cleans the XLSX file and saves the clean data in a CSV
4. A CSV file created from the data processed in the IPYNB file 
5. A mavenproject folder that consists of :
   * mavenproject/src/main/java/mavenproject
      * mavenproject/src/main/java/mavenproject/App.java
   * mavenproject/pom.xml
   
## Python script functionality
In the .py file i use the playwright library to create a semi-manual browser automation tool that searches in a booking url that contains some specific filters:
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

*The only part that is not automated is the clicking of the load more button*

**The .py file and the resulting data are shared solely for educational purposes. This script is not intended for commercial use, and the data provided is for learning and experimentation only**

## The IPYNB file
In the IPYNB file, the raw data collected through web scraping is transformed into a structured format (x, y), where:
* x represents the price.
* y represents the distance from the city center.

(*For more details, please refer to the IPYNB file where the process is made clear through visualization of each step*)

## Maven Project
> This project uses the davidmoten/rtree library to build an R-tree and compute skylines
### 1.R-tree construction:
   * Reads points from the final_hotels_dist.csv
   * Creates an R-tree using quadratic split(max 4 children/node)
     _(TODO: experiment with other tree configurations)_

### 2.Skyline computation:
   * Implements Branch and Bound algorithm -> O(n*log(n)).
   * Compares results with a brute-force approach(comparing each point with all the others-> O(n^2))

### 3.Synthetic data:
   * Tests performance using data with different correlation types (strong positive, strong negative, zero).




