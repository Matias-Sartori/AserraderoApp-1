;This file will be executed next to the application bundle image
;I.e. current directory will contain folder AserraderoApp with application files
[Setup]
AppId={{fxApplication}}
AppName=AserraderoApp
AppVersion=1.0
AppVerName=AserraderoApp 1.0
AppPublisher=aserradero
AppComments=Aserradero_2
AppCopyright=Copyright (C) 2020
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={localappdata}\AserraderoApp
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=aserradero
;Optional License
LicenseFile=
;(Windows 2000/XP/Server 2003 are no longer supported.)
MinVersion=6.0
OutputBaseFilename=AserraderoApp-1.0
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=AserraderoApp\AserraderoApp.ico
UninstallDisplayIcon={app}\AserraderoApp.ico
UninstallDisplayName=AserraderoApp
WizardImageStretch=No
WizardSmallImageFile=AserraderoApp-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64
Password=3333

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "AserraderoApp\AserraderoApp.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "AserraderoApp\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\AserraderoApp"; Filename: "{app}\AserraderoApp.exe"; IconFilename: "{app}\AserraderoApp.ico"; Check: returnTrue()
Name: "{commondesktop}\AserraderoApp"; Filename: "{app}\AserraderoApp.exe";  IconFilename: "{app}\AserraderoApp.ico"; Check: returnFalse()


[Run]
Filename: "{app}\AserraderoApp.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\AserraderoApp.exe"; Description: "{cm:LaunchProgram,AserraderoApp}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\AserraderoApp.exe"; Parameters: "-install -svcName ""AserraderoApp"" -svcDesc ""AserraderoApp"" -mainExe ""AserraderoApp.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\AserraderoApp.exe "; Parameters: "-uninstall -svcName AserraderoApp -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
