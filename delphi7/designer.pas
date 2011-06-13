unit designer;

interface

uses
   Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
   myDialogs, ComCtrls, StdCtrls, Grids, ExtCtrls, Menus, ImgList, Buttons,
   IniFiles, Dialogs;

type
   TfrmMapDesigner = class(TForm)
      pm: TPopupMenu;
      Home1: TMenuItem;
      Yes1: TMenuItem;
      No1: TMenuItem;
      Man2: TMenuItem;
      Block1: TMenuItem;
      Blank1: TMenuItem;
      Panel6: TPanel;
      Panel2: TPanel;
      Panel1: TPanel;
      Panel3: TPanel;
      BitBtn6: TBitBtn;
      Panel8: TPanel;
      e_level: TEdit;
      levelUpDown: TUpDown;
      btnActionSave: TBitBtn;
      pc: TPageControl;
      TabSheet1: TTabSheet;
      dg: TDrawGrid;
      TabSheet2: TTabSheet;
      N1: TMenuItem;
      od: TOpenDialog;
      sd: TSaveDialog;
      Panel5: TPanel;
      btnMapImport: TBitBtn;
      btnMapExport: TBitBtn;
      Panel10: TPanel;
      Panel11: TPanel;
      Panel12: TPanel;
      Panel13: TPanel;
      Panel14: TPanel;
      Panel15: TPanel;
      Panel16: TPanel;
      Panel17: TPanel;
      Panel18: TPanel;
      Panel19: TPanel;
      Panel20: TPanel;
      Panel21: TPanel;
      Panel22: TPanel;
      Panel23: TPanel;
      Panel24: TPanel;
      Panel25: TPanel;
      Label2: TLabel;
      Label3: TLabel;
      Label4: TLabel;
      Label5: TLabel;
      Label6: TLabel;
      Label7: TLabel;
      Label8: TLabel;
      Label9: TLabel;
      Label10: TLabel;
      Label11: TLabel;
      Label12: TLabel;
      Label13: TLabel;
      Label14: TLabel;
      Label15: TLabel;
      Label16: TLabel;
      Label17: TLabel;
      Panel26: TPanel;
      Label18: TLabel;
      Label19: TLabel;
      Label20: TLabel;
      Label21: TLabel;
      Label22: TLabel;
      Label23: TLabel;
      Label24: TLabel;
      Label25: TLabel;
      Label26: TLabel;
      Label27: TLabel;
      Label28: TLabel;
      Label29: TLabel;
      Label30: TLabel;
      Label31: TLabel;
      Label32: TLabel;
      Label33: TLabel;
      Panel27: TPanel;
      Panel28: TPanel;
      Panel29: TPanel;
      Panel30: TPanel;
      Panel31: TPanel;
      Panel32: TPanel;
      Panel33: TPanel;
      Panel34: TPanel;
      Panel35: TPanel;
      Panel36: TPanel;
      Panel37: TPanel;
      Panel38: TPanel;
      x03: TPanel;
      x02: TPanel;
      x01: TPanel;
      sg: TStringGrid;
      Panel4: TPanel;
      Label34: TLabel;
      Label35: TLabel;
      Label36: TLabel;
      Label37: TLabel;
      Label38: TLabel;
      Label39: TLabel;
      Label40: TLabel;
      Label41: TLabel;
      Label42: TLabel;
      Label43: TLabel;
      Label44: TLabel;
      Label45: TLabel;
      Label46: TLabel;
      Label47: TLabel;
      Label48: TLabel;
      Label49: TLabel;
      Panel7: TPanel;
      Panel9: TPanel;
      Panel39: TPanel;
      Panel40: TPanel;
      Panel41: TPanel;
      Panel42: TPanel;
      Panel43: TPanel;
      Panel44: TPanel;
      Panel45: TPanel;
      Panel46: TPanel;
      Panel47: TPanel;
      Panel48: TPanel;
      Panel49: TPanel;
      Panel50: TPanel;
      Panel51: TPanel;
      N2: TMenuItem;
      Cancel1: TMenuItem;
      btnMapClear: TBitBtn;
      sb0: TSpeedButton;
      sb1: TSpeedButton;
      sb2: TSpeedButton;
      sb3: TSpeedButton;
      sb4: TSpeedButton;
      sb6: TSpeedButton;
      ClearMap1: TMenuItem;
      Label1: TLabel;
      Panel68: TPanel;
      Label66: TLabel;
      Panel69: TPanel;
      Label67: TLabel;
      Panel70: TPanel;
      Label68: TLabel;
      Panel71: TPanel;
      Label69: TLabel;
      Panel72: TPanel;
      Label70: TLabel;
      Panel73: TPanel;
      Label71: TLabel;
      Panel74: TPanel;
      Label72: TLabel;
      Panel75: TPanel;
      Label73: TLabel;
      Panel76: TPanel;
      Label74: TLabel;
      Panel77: TPanel;
      Label76: TLabel;
      Panel78: TPanel;
      Label77: TLabel;
      Panel79: TPanel;
      Label78: TLabel;
      Panel80: TPanel;
      Label79: TLabel;
      Panel81: TPanel;
      Panel82: TPanel;
      Panel52: TPanel;
      Label50: TLabel;
      Label51: TLabel;
      Label52: TLabel;
      Label53: TLabel;
      Label54: TLabel;
      Label55: TLabel;
      Label56: TLabel;
      Label57: TLabel;
      Label58: TLabel;
      Label59: TLabel;
      Label60: TLabel;
      Label61: TLabel;
      Label62: TLabel;
      Label63: TLabel;
      Label64: TLabel;
      Label65: TLabel;
      Label75: TLabel;
      Label80: TLabel;
      Label81: TLabel;
      Label82: TLabel;
      Label83: TLabel;
      Panel53: TPanel;
      Panel54: TPanel;
      Panel55: TPanel;
      Panel56: TPanel;
      Panel57: TPanel;
      Panel58: TPanel;
      Panel59: TPanel;
      Panel60: TPanel;
      Panel61: TPanel;
      Panel62: TPanel;
      Panel63: TPanel;
      Panel64: TPanel;
      Panel65: TPanel;
      Panel66: TPanel;
      Panel67: TPanel;
      Panel83: TPanel;
      Panel84: TPanel;
      Panel85: TPanel;
      Panel86: TPanel;
      Panel87: TPanel;
      procedure dgDblClick(Sender: TObject);
      procedure FormDestroy(Sender: TObject);
      procedure pmClick(Sender: TObject);
      procedure FormClose(Sender: TObject; var Action: TCloseAction);
      procedure BitBtn6Click(Sender: TObject);
      procedure sb0Click(Sender: TObject);
      procedure FormCreate(Sender: TObject);
      procedure levelUpDownClick(Sender: TObject; Button: TUDBtnType);
      procedure btnActionSaveClick(Sender: TObject);
      procedure pcChange(Sender: TObject);
      procedure btnMapImportClick(Sender: TObject);
      procedure btnMapExportClick(Sender: TObject);
      procedure dgKeyDown(Sender: TObject; var Key: Word;
         Shift: TShiftState);
      procedure FormPaint(Sender: TObject);
      procedure Cancel1Click(Sender: TObject);
      procedure btnMapClearClick(Sender: TObject);
      procedure dgMouseMove(Sender: TObject; Shift: TShiftState; X,
         Y: Integer);
      procedure ClearMap1Click(Sender: TObject);
      procedure dgMouseDown(Sender: TObject; Button: TMouseButton;
         Shift: TShiftState; X, Y: Integer);
      procedure FormKeyDown(Sender: TObject; var Key: Word;
         Shift: TShiftState);
   private
    { Private declarations }
      imgIndex: integer;

   public
    { Public declarations }
      impMap: TINIFile;
   end;

var
   frmMapDesigner: TfrmMapDesigner;

implementation

uses main, uPopUp, common;

{$R *.dfm}


procedure TfrmMapDesigner.dgDblClick(Sender: TObject);
begin
   if levelUpDown.Position < 1 then
   begin
      MessageDlgTimer('먼저 단계를 조정하십시오.');
      exit;
   end;

   try
      SG.Cells[DG.Col, DG.Row] := intTostr(imgIndex);
   finally
      DrawMap(dg, sg, frmMain.iml);
   end;
end;

procedure TfrmMapDesigner.FormDestroy(Sender: TObject);
begin
   frmMapDesigner := nil;
end;

procedure TfrmMapDesigner.pmClick(Sender: TObject);
begin
   if levelUpDown.Position < 1 then
   begin
      MessageDlgTimer('먼저 단계를 조정하십시오.');
      exit;
   end;

   imgIndex := TMenuItem(Sender).ImageIndex;
   TMenuItem(Sender).Checked := true;
   case imgIndex of
      0: sb0.Down := true;
      1: sb1.Down := true;
      2: sb2.Down := true;
      3: sb3.Down := true;
      4: sb4.Down := true;
      6: sb6.Down := true;
   end;
   dgDblClick(DG);
end;

procedure TfrmMapDesigner.FormClose(Sender: TObject;
   var Action: TCloseAction);
begin
   Action := caFree;
end;

procedure TfrmMapDesigner.BitBtn6Click(Sender: TObject);
begin
   Self.ModalResult := mrOk;
end;

procedure TfrmMapDesigner.sb0Click(Sender: TObject);
begin
   TSpeedButton(Sender).Down := True;
   imgIndex := TSpeedButton(Sender).Tag;
   pm.Items[imgIndex].Checked := True;
end;

procedure TfrmMapDesigner.FormCreate(Sender: TObject);
begin
   SetWindowLong(Application.Handle, GWL_EXSTYLE,
      GetWindowLong(Application.Handle, GWL_EXSTYLE)
      or WS_EX_TOOLWINDOW
      and not WS_EX_APPWINDOW);

   frmMain.iml.GetBitmap(0, sb0.Glyph);
   frmMain.iml.GetBitmap(1, sb1.Glyph);
   frmMain.iml.GetBitmap(2, sb2.Glyph);
   frmMain.iml.GetBitmap(3, sb3.Glyph);
   frmMain.iml.GetBitmap(4, sb4.Glyph);
   frmMain.iml.GetBitmap(6, sb6.Glyph);

   Self.levelUpDown.Position := frmMain.curLevel;
   Self.levelUpDownClick(Self.levelUpDown, btNext);
end;

procedure TfrmMapDesigner.levelUpDownClick(Sender: TObject;
   Button: TUDBtnType);
var
   Section: string;
begin
   Section := formatfloat('000', levelUpDown.Position);

   if not frmMain.map.SectionExists(Section) then
   begin
      StringGridClear(sg);
      DrawGridClear(dg);
      MapWrite(sg, frmMain.map, levelUpDown.Position);
   end;

   e_level.Text := intTostr(levelUpDown.Position);
   common.ResetMap(dg, sg, frmmain.map, frmMain.iml, levelUpDown.Position);
end;

procedure TfrmMapDesigner.btnActionSaveClick(Sender: TObject);
begin
   if levelUpDown.Position < 1 then
   begin
      MessageDlgTimer('먼저 단계를 조정하십시오.');
      exit;
   end;

   MapWrite(sg, frmMain.map, levelUpDown.Position);
end;

procedure TfrmMapDesigner.pcChange(Sender: TObject);
begin
   if pc.ActivePageIndex = 0 then
      DrawMap(dg, sg, frmMain.iml);
end;

procedure TfrmMapDesigner.btnMapImportClick(Sender: TObject);
var
   Tot, i: integer;
   section, ident, mapData: string;
const
   identPreFix: string = 'line';
begin
   if levelUpDown.Position < 1 then
   begin
      MessageDlgTimer('먼저 단계를 조정하십시오.');
      exit;
   end;

   od.InitialDir := ExtractFilePath(paramstr(0));
   if not od.Execute then exit;


   impMap := TINIFile.Create(od.FileName);
   try
      try
         DrawGridClear(dg);

         frmPopup := TfrmPopup.Create(Self);
         frmPopup.ShowModal;

         if frmPopup.ModalResult = mrOk then
         begin
            section := frmPopup.cbLevel.Text;
            frmPopup.Free;
         end else
         begin
            frmPopup.Free;
            exit;
         end;

         if not frmMain.map.SectionExists(section) then
         begin
            MapWrite(sg, frmMain.map, strToint(section));
         end;

         StringGridClear(sg);
         DrawGridClear(dg);

         tot := impMap.ReadInteger(section, 'line00', 0);

         for i := 0 to tot - 1 do
         begin
            ident := identPreFix + formatfloat('00', i + 1);
            mapData := impMap.ReadString(section, ident, '');
            LoadMap(sg, mapData, i);
            DrawMap(dg, sg, frmMain.iml);
         end;
      except
         on e: exception do
         begin
            MessageDlgTimer(e.message);
         end;
      end;
   finally
      impMap.Free;
   end;
end;

procedure TfrmMapDesigner.btnMapExportClick(Sender: TObject);
begin
   if levelUpDown.Position < 1 then
   begin
      MessageDlgTimer('먼저 단계를 조정하십시오.');
      exit;
   end;

   sd.InitialDir := ExtractFilePath(paramstr(0));
   if not sd.Execute then Exit;

   impMap := TINIFile.Create(sd.FileName);
   try
      MapWrite(sg, impMap, levelUpDown.Position);
   finally
      impMap.Free;
   end;
end;

procedure TfrmMapDesigner.dgKeyDown(Sender: TObject; var Key: Word;
   Shift: TShiftState);
begin
   if key = vk_escape then
   begin
      DrawGridClear(dg);
      drawMap(dg, sg, frmMain.iml);
   end;
end;

procedure TfrmMapDesigner.FormPaint(Sender: TObject);
begin
   DrawMap(dg, sg, frmMain.iml);
end;

procedure TfrmMapDesigner.Cancel1Click(Sender: TObject);
begin
   DrawMap(dg, sg, frmMain.iml);
end;

procedure TfrmMapDesigner.btnMapClearClick(Sender: TObject);
begin
   StringGridClear(sg);
   DrawGridClear(dg);
   MapWrite(sg, frmMain.map, levelUpDown.Position);
end;

procedure TfrmMapDesigner.dgMouseMove(Sender: TObject; Shift: TShiftState;
   X, Y: Integer);
var
   R: TRect;
   col, row: integer;
   P: TPoint;
begin
   if Shift = [ssLeft] then
   begin
      for row := 0 to dg.RowCount - 1 do
      begin
         for col := 0 to dg.ColCount - 1 do
         begin

            P := Point(x, y);
            R := dg.CellRect(col, row);
            if ptInRect(R, P) then
            begin
               dg.Canvas.Brush.Color := dg.Color;
               dg.Canvas.FillRect(R);

               frmMain.iml.getBitmap(imgIndex, frmMain.bmp);
               dg.Canvas.BrushCopy(R, frmMain.bmp, frmMain.bmp.Canvas.ClipRect, clWhite);
               sg.Cells[col, row] := intTostr(imgIndex);
            end;
         end;
      end;
   end;
end;

procedure TfrmMapDesigner.ClearMap1Click(Sender: TObject);
begin
   StringGridClear(sg);
   DrawGridClear(dg);
end;

procedure TfrmMapDesigner.dgMouseDown(Sender: TObject;
   Button: TMouseButton; Shift: TShiftState; X, Y: Integer);
var
   R: TRect;
   col, row: integer;
   P: TPoint;
begin
   P := Point(x, y);
   for row := 0 to dg.RowCount - 1 do
   begin
      for col := 0 to dg.ColCount - 1 do
      begin
         R := dg.CellRect(col, row);
         if ptInRect(R, P) then
         begin
            sg.Col := col;
            sg.Row := row;
            dg.Col := col;
            dg.Row := row;
            exit;
         end;
      end;
   end;
end;

procedure TfrmMapDesigner.FormKeyDown(Sender: TObject; var Key: Word;
   Shift: TShiftState);
begin
   case key of
      vk_Prior: begin // page up
            if 255 > Self.AlphaBlendValue then
               Self.AlphaBlendValue := Self.AlphaBlendValue + 5;
            exit;
         end;
      vk_Next: begin // page down
            if 50 < Self.AlphaBlendValue then
               Self.AlphaBlendValue := Self.AlphaBlendValue - 5;
            exit;
         end;
      vk_Home: begin
            Self.AlphaBlendValue := 255;
            exit;
         end;
      vk_End: begin
            Self.AlphaBlendValue := 50;
            exit;
         end;
   end;
end;

end.

