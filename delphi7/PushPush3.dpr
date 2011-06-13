program PushPush3;

uses
  Forms,
  Windows,
  Dialogs,
  myDialogs,
  main in 'main.pas' {frmMain},
  common in 'common.pas',
  designer in 'designer.pas' {frmMapDesigner},
  uPopUp in 'uPopUp.pas' {frmPopup};//,
  //myDialogs in 'D:\Program Files\Borland\Delphi6\Lib\myDialogs.pas';

{$R *.res}

var
   Mutex : THandle;
begin
   Mutex := CreateMutex(nil, True, 'PushPushIII');

   if (Mutex <> 0 ) and (GetLastError = 0) then
   begin
      Application.Initialize;

      Application.CreateForm(TfrmMain, frmMain);
      Application.Run;

      if Mutex <> 0 then CloseHandle(Mutex);
   end else
   begin
      MessageDlgTimer('Push Push III가 이미 실행중입니다.',mtInformation,[mbOk],0,-1,-1,'',3000);
   end;
end.
