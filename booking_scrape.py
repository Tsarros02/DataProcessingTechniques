from playwright.sync_api import sync_playwright
import pandas as pd
import time


def main():
    with sync_playwright() as p:
        #specify future dates
        checkin_date = '2025-01-10'
        checkout_date = '2025-01-11'
        #the page url can have specific filters  
        page_url = f'https://www.booking.com/searchresults.html?label=gen173nr-1FCAEoggI46AdIM1gEaFyIAQGYATG4ARfIARXYAQHoAQH4AQKIAgGoAgO4AqHn4bsGwAIB0gIkZDI5MDRkY2QtZDU2OS00ODIzLThmMjctMWFhZjBlYzE0NDdm2AIF4AIB&aid=304142&ss=Athens&ssne=Athens&ssne_untouched=Athens&efdco=1&lang=en-us&src=searchresults&dest_id=-814876&dest_type=city&checkin={checkin_date}&checkout={checkout_date}&group_adults=1&no_rooms=1&group_children=0&nflt=distance%3D3000&soz=1&lang_changed=1'
        #the browser will pop-up
        browser = p.chromium.launch(headless=False)
        page = browser.new_page()
        #100 seconds max delay for the page to open
        page.goto(page_url, timeout=100000)
            
        #hotels_list will store each hotel with the info specified below 
        hotels_list = []
        #create a set for the hotels already in use 
        scraped_hotels = set()
        max_scrolls = 100
        current_scrolls = 0
        #wait 5 seconds for the page to load
        time.sleep(5)
        while current_scrolls<=max_scrolls:
            hotels = page.locator('//div[@data-testid="property-card"]').all()
            #print the total number of hotels scraped every 10 scrolls
            if (current_scrolls%10==0 and current_scrolls>=10):
                print(f'There are: {len(hotels)} hotels.')

            for hotel in hotels:
                hotel_dict = {}
                #store the hotel name
                hotel_dict['hotel'] = hotel.locator('//div[@data-testid="title"]').inner_text()
                #if the hotel is already scraped continue to the next
                if hotel_dict['hotel'] in scraped_hotels:
                    continue
                #store the hotel price and distance
                hotel_dict['price'] = hotel.locator('//span[@data-testid="price-and-discounted-price"]').inner_text()
                hotel_dict['distance'] = hotel.locator('//span[@data-testid="distance"]').inner_text()
                hotels_list.append(hotel_dict)
                scraped_hotels.add(hotel_dict['hotel'])


            

            page.evaluate('window.scrollBy(0, 2000)')  #scroll 2000px down
            time.sleep(0.5)  #wait for content to load after each scroll
            #after 10 scrolls the load more content will show up  
            if current_scrolls>10:
                time.sleep(1.5) #add a bigger delay for the user to manually click the button
            current_scrolls +=1
            

        #save the scraped data(dataframe to excel)
        df = pd.DataFrame(hotels_list)
        df.to_excel('hotels_price_dist.xlsx', index=False) 
        browser.close()
            
if __name__ == '__main__':
    main()
