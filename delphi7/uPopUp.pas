unit uPopUp;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  myDialogs, StdCtrls, Buttons;

type
  TfrmPopup = class(TForm)
    cbLevel: TComboBox;
    btnImport: TBitBtn;
    Label1: TLabel;
    lblFileName: TLabel;
    btnCancel: TBitBtn;
    procedure FormCreate(Sender: TObject);
    procedure btnImportClick(Sender: TObject);
    procedure btnCancelClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmPopup: TfrmPopup;

implementation

uses designer;

{$R *.dfm}

procedure TfrmPopup.FormCreate(Sender: TObject);
var
   Section : TStrings;
   i : integer;
begin
   lblFileName.Caption := frmMapDesigner.od.FileName;
   Section := TStringList.Create;
   try
      frmMapDesigner.impMap.ReadSections(Section);
      cbLevel.Items.Clear;
      for i:=0 to Section.Count - 1 do
      begin
         cbLevel.Items.Add(Section.Strings[i]);
      end;
      if Section.Count = 0 then
      begin
         MessageDlgTimer('선택하신 파일에 맵 정보가 존재하지 않거나 잘못된 파일입니다.');
         ModalResult := mrCancel;
      end;
      Section.Free;
   except
      Section.Free;
   end;
end;

procedure TfrmPopup.btnImportClick(Sender: TObject);
begin
   if cbLevel.ItemIndex >= 0 then
   begin
      ModalResult := mrOk;
   end else
   begin
      MessageDlgTimer('Import하실 단계를 선택하십시오.');
      cbLevel.SetFocus;
      exit;
   end;
end;

procedure TfrmPopup.btnCancelClick(Sender: TObject);
begin
   ModalResult := mrCancel;
end;

end.
