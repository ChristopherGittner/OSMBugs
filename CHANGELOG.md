CHANGELOG
=========
V 1-2-3

- Libraries Updated
- Bug Fixed: Using the Zoom Buttons while the map is following the GPS position lead to a crash

V 1-2-2

- Show a Message when adding a new Bug as a hint how to add new Bugs

V 1-2-1

- Enable Cleartext communication for http on Devices with Android 9 and above. See GitHub issue #22  

V 1-2-0

- Added an Option to set the maximum cache size of the tile cache. A 20 MB tolerance is set as a threshold 

V 1-1-6

- Increased the read Timeout for Osmose to 40 seconds, since some requests do take a long time to be processed
- Update AndroidAnnotations to 4.6.0

V 1-1-5

- Fixed a Bug: Mapnik tiles did not load, because the correct User-Agent was not set.
- Update Osmdroid to 6.1.0

V 1-1-4

- Fixed a translation Error

V 1-1-3

- Updated Android Annotations Library

V 1-1-2

- Fixed Bug: Calls to the Mapdust API with no results falsely showed the Message: "Failed to load from Mapdust"git 

V 1-1-1

- Tiles would not load if the default tile cache (external storage) was not writable. Changed the tile cache location to a internal folder.

V 1-1-0

- Added creation date to all Bug types

V 1-0-0

- Osm Notes is now the default Platformin the Bug list view
- Added a "Default Bug Platform" for new bug creation
- In the Bug List View, Osm Notes is now the default (first) tab
- Modified the behaviour of the Follow GPS option. (Activating it now centers on the last fix, and follows the GPS position until the map is moved)
- Bugs can now be added by long-clicking on the map
- Removed the Debug server option
- Removed the option to enable or disable the max zoom level override, since this is now handled by the osmdroid library

V 0-9-8

- Added an option to enable or disable the max zoom level override
- Added an option to clear the tile cache manually
- Fixed the floating action button icon on the main map

V 0-9-6

- Added changelog
- Increased maximum Zoom Level by 3
