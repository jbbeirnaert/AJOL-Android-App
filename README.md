# AJOL-Android-App
Ajol Paper is an android mobile application that allows the user to customize the device's wallpaper so it changes depending on the device's location. 

## Known Issues
- [ ] __Device location__: the lastKnownLocation() function that we use doesn't actually determine the device's location, but rather check's whatever position was returned by the last app on the phone that determined it. This means that if Ajol Paper is the first application opened after connecting to a network, the device's location is unknown.
- [ ] __Images__: we can't store URI references to the photo gallery in the database because they have limited access time-wise. Once the activity that pulled that image from the gallery ends, so does the access with that URI. We also can't really store copies of the images in the database because SQLite cursors have a size limit of 1MB. Images larger than that cause errors when the app tries to read from that cursor. This is what Ajol Paper does currently. The solution appears to be to store a copy of the image in a separate folder in the device file system so URI's to those files have unlimited permissions in the app.
- [ ] __Map Display__: The existing wallpapers are not displayed on the map the first time the app is opened. It should be reloaded after permissions are granted. Newly added/modified wallpapers also don't show on the settings activity map, which might be fixed if SettingsActivity had a publicly accessible static variable to keep track of whether the map should be reloaded.
- [ ] __Map Interaction__: If the user clicks on a point in the map in settings the modify/add activity should be started and passed the wallpaper info or new location.
- [ ] __Photo Selection__: When the user selects a photo in the modify activity the onActivityResult() method from the photo picker stalls the UI thread. I'm not completely sure why. I tried to use AsyncTasks but I'm not sure if I used them correctly.

## Tasks
- [ ] User Interface
	- [x] main/settings layout
		- [x] include map
		- [x] defaults toggle
		- [x] refresh time input field
		- [x] wallpapers button
		- [x] defaults button
		- [x] save button
	- [x] list layout
		- [x] toolbar
			- [x] search bar
			- [x] navigation to settings
		- [x] list item
			- [x] wallpaper name
			- [x] image
			- [x] radius
			- [x] edit button
			- [x] delete button
		- [ ] list-empty view
		- [x] scrollable
		- [x] clickable
        - [x] use item.image
	- [x] modify layout
		- [x] include map
		- [x] wallpaper info
		- [x] save button
        - [x] map displays wallpapers
        - [x] map.onclick() -> changes desired x and y coordinates
	- [x] link activities together
		- [x] settings -> modify
		- [x] settings -> list
		- [x] list -> settings
		- [x] list -> modify
		- [x] modify -> settings
		- [x] modify -> list
- [x] Persistent Storage
- [x] Enable UI buttons
	- [x] settings
	- [x] list
	- [x] modify
- [ ] Background App
    - [x] track device location
    - [ ] compare location to wallpapers.locations
    - [x] update device wallpaper
- [x] Device Location Integration
	- [x] ask permission in activity
	- [x] google maps api key
