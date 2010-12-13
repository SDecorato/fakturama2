linux_32bit and linux_64bit: 
	Copy the Fakturama folder into the fakturama/usr/share folder.
	Modify the version in the create_deb script and in fakturama/DEBIAN/control.
	Run the create_deb script.

osx_64bit:
    Pack a Fakturama Folder created under Linux in a tar.gz archive to transfer it to OS X.
	Copy the Fakturama folder into the osx_64bit folder.
	Tab Configuration: Modify the "Package Version" in the PackageManager Fakturama Contents.
	Tab Configuration: Destination: /Applications
	Tab Configuration: Remove "Require admin authentication"
	Tab Contents: "Apply Recommendations"
	Tab Contents: Include root in package
	Tab Components: Do not "Allow Relocation" (maybe you have to save and reopen the PackageMaker)
	Build it.
	Change the icon: support.apple.com/kb/TA20788. Use win_Installer.ico
	Create a DMG with "disk utility.app"
	New Image
	Size: Size of PKG + 5MBytes.
	Name "Fakturama_OS_X_64Bit_1_0_0"
	Save As: "Fakturama_OS_X_64Bit_1_0_0.dmg"
	Drag&Drop the *.pkg Installer onto the Disk Image
	
	
win_32bit and win_64bit
	Use NSIS with the AccessControl Plugin.
    Pack a Fakturama Folder created under Linux in a tar.gz archive to transfer it to OS X.
	Copy the Fakturama folder into the NSIS folder.
	Start NSIS - Compile NSI Scripts.
	Modify the version in the Fakturama.nsi script.
	Drag&Drop the Fakturama.nsi script into the Text field of MakeNSISW
	ZIP the installer.
	
	
	
