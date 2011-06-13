unit main;

interface

uses
   Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
   myDialogs, Dialogs, ExtCtrls, StdCtrls, Buttons, Grids, types, ImgList, inifiles,
   Menus, ComCtrls, ShellAPI, VIdle;

type
   TfrmMain = class(TForm)
      sg: TStringGrid;
      iml: TImageList;
      trayPop: TPopupMenu;
      Close1: TMenuItem;
      N1: TMenuItem;
      ActList: TMemo;
      playList: TMemo;
      ActionPlay1: TMenuItem;
      N2: TMenuItem;
      SaveAction: TMenuItem;
      LoadAction: TMenuItem;
      Refresh1: TMenuItem;
      PlayTimer: TTimer;
      Start1: TMenuItem;
      N3: TMenuItem;
      N4: TMenuItem;
      StayOnTop1: TMenuItem;
      Help1: TMenuItem;
      About1: TMenuItem;
      N5: TMenuItem;
      dg: TDrawGrid;
      mHelp: TMemo;
      pl_msg: TPanel;
      N6: TMenuItem;
      MapDesigner1: TMenuItem;
      idleTimer: TVIdleTimer;
      procedure FormClose(Sender: TObject; var Action: TCloseAction);
      procedure FormDestroy(Sender: TObject);
      procedure FormCreate(Sender: TObject);
      procedure FormKeyDown(Sender: TObject; var Key: Word;
         Shift: TShiftState);
      procedure Close1Click(Sender: TObject);
      procedure PlayTimerTimer(Sender: TObject);
      procedure Refresh1Click(Sender: TObject);
      procedure ActionPlay1Click(Sender: TObject);
      procedure SaveActionClick(Sender: TObject);
      procedure LoadActionClick(Sender: TObject);
      procedure Start1Click(Sender: TObject);
      procedure StayOnTop1Click(Sender: TObject);
      procedure FormPaint(Sender: TObject);
      procedure Help1Click(Sender: TObject);
      procedure About1Click(Sender: TObject);
      procedure mHelpDblClick(Sender: TObject);
      procedure mHelpKeyDown(Sender: TObject; var Key: Word;
         Shift: TShiftState);
      procedure MapDesigner1Click(Sender: TObject);
      procedure FormCloseQuery(Sender: TObject; var CanClose: Boolean);
      procedure idleTimerTimer(Sender: TObject);
   private
    { Private declarations }
      procedure ActionUndo(ActionHistory: TMemo; Auto: Boolean);
      procedure WriteLog(ACol, ARow: integer; Value: string);
      procedure WMNCHitTest(var Msg: TWMNCHitTest); message wm_NCHitTest;
      procedure ADDIcon(newICON: HICON);
      procedure ChangeIcon(newICON: HICON);
      procedure DeleteIcon;
      procedure HelpView;
      procedure levelMove(value: integer);
   public
    { Public declarations }
      curLevel: integer;
      bmp: TBitmap;
      map: TINIFile;
      procedure WndProc(var Message: TMessage); override;
   end;

var
   frmMain: TfrmMain;
   iconData: TNotifyIconData;

const
   MY_SHOWFORM_ID = WM_USER + 1;

implementation
uses
   common, designer;

{$R *.dfm}

procedure TfrmMain.WndProc(var Message: TMessage);
var
   p: TPoint;
begin

   case Message.Msg of
      WM_QUERYENDSESSION:
         begin
            Message.Result := LongInt(True);
         end;
      MY_SHOWFORM_ID: begin
            case Message.lParam of
               WM_LBUTTONDBLCLK: begin
                     Self.Show;
                     idleTimer.Enabled := true;
                     Self.Start1Click(Self.Start1);
                  end;
               WM_RBUTTONDOWN: begin
                     GetCursorPos(p);
                     TrayPop.Popup(p.x, p.y);
                  end;
            end;
         end;
   end;
   inherited;
end;

procedure TfrmMain.WMNCHitTest(var Msg: TWMNCHitTest);
begin
   inherited;

   with Msg do
   begin
      if Result = htClient then Result := htCaption;
   end;
end;

procedure TfrmMain.FormCreate(Sender: TObject);
begin
   try
      SetWindowLong(Application.Handle, GWL_EXSTYLE,
         GetWindowLong(Application.Handle, GWL_EXSTYLE)
         or WS_EX_TOOLWINDOW and not WS_EX_APPWINDOW);

      ADDIcon(Application.Icon.Handle);

      bmp := TBitmap.Create;
      map := TINIFile.Create(ExtractFilePath(paramstr(0)) + 'map.ini');

      curLevel := ReadCurrentLevel(map);

      Self.Caption := 'Push Push III - ' + intTostr(curLevel) + '단계';

      dg.Left := 0;
      dg.Top := 0;
      Self.ClientWidth := dg.Width;
      Self.ClientHeight := dg.Height + pl_msg.Height;

   finally
      Self.Start1Click(Self.Start1);
   end;
end;

procedure TfrmMain.FormClose(Sender: TObject; var Action: TCloseAction);
begin
   DeleteIcon;

   try bmp.free; except end;
   try map.Free; except end;
   Action := caFree;
end;

procedure TfrmMain.FormDestroy(Sender: TObject);
begin
   frmMain := nil;
end;

procedure TfrmMain.FormKeyDown(Sender: TObject; var Key: Word;
   Shift: TShiftState);
var
   newValue, oldValue, nxtValue: string;
   imgIdx: integer;
   oldCol, oldRow: integer;
   newCol, newRow: integer;
   nxtCol, nxtRow: integer;
begin
   if playTimer.Enabled then
   begin
      case key of
         vk_F4: begin // action replay
               ActionPlay1Click(Self.ActionPlay1);
            end;
         vk_F10: begin
               ActionPlay1Click(Self.ActionPlay1);
               ChangeIcon(Application.Icon.Handle);
               Self.Hide;
            end;
         vk_Up: begin
               if playTimer.Interval >= 50 then
               begin
                  playTimer.Interval := playTimer.Interval - 10;
               end;
            end;
         vk_Down: begin
               if playTimer.Interval <= 1000 then
               begin
                  playTimer.Interval := playTimer.Interval + 10;
               end;
            end;
         vk_Prior: begin // page up
               if 255 > Self.AlphaBlendValue then
                  Self.AlphaBlendValue := Self.AlphaBlendValue + 5;
            end;
         vk_Next: begin // page down
               if 50 < Self.AlphaBlendValue then
                  Self.AlphaBlendValue := Self.AlphaBlendValue - 5;
            end;
         vk_Home: begin
               Self.AlphaBlendValue := 255;
            end;
         vk_End: begin
               Self.AlphaBlendValue := 50;
            end;
      end;
      exit;
   end;

   pl_msg.Caption := '';
   newCol := sg.Col;
   newRow := sg.Row;
   oldCol := sg.Col;
   oldRow := sg.Row;
   nxtCol := sg.Col;
   nxtRow := sg.Row;
   if (Shift = [ssShift]) and (key in [37..40]) then
   begin
      case key of
         vk_Left: begin
               Left := Left - 5;
            end;
         vk_Up: begin
               Top := Top - 5;
            end;
         vk_Right: begin
               Left := Left + 5;
            end;
         vk_Down: begin
               Top := Top + 5;
            end;
      end;
      exit;
   end;

   case key of
      vk_Left: begin
            newCol := oldCol - 1;
            nxtCol := newCol - 1;
         end;
      vk_Up: begin
            newRow := oldRow - 1;
            nxtRow := newRow - 1;
         end;
      vk_Right: begin
            newCol := oldCol + 1;
            nxtCol := newCol + 1;
         end;
      vk_Down: begin
            newRow := oldRow + 1;
            nxtRow := newRow + 1;
         end;
      vk_F1: begin // help
            HelpView;
            Exit;
         end;
      vk_F2: begin // load
            LoadActionClick(Self.LoadAction);
            Exit;
         end;
      vk_F3: begin // save
            SaveActionClick(Self.SaveAction);
            Exit;
         end;
      vk_F4: begin // action replay
            ActionPlay1Click(Self.ActionPlay1);
         end;
      vk_F5: begin // action replay
            Refresh1Click(Self.Refresh1);
            Exit;
         end;
      vk_F6: begin // prior
            levelMove(-1);
            Exit;
         end;
      vk_F7: begin // next
            levelMove(1);
            Exit;
         end;
      vk_F10: begin
            ChangeIcon(Application.Icon.Handle);
            Self.Hide;
            Exit;
         end;
      vk_F11: begin
            Self.MapDesigner1Click(Self.MapDesigner1);
            exit;
         end;
      vk_F12: begin
            Self.Tag := 99;
            Close;
            exit;
         end;
      vk_Prior: begin // page up
            if 255 > Self.AlphaBlendValue then
               Self.AlphaBlendValue := Self.AlphaBlendValue + 5;
         end;
      vk_Next: begin // page down
            if 50 < Self.AlphaBlendValue then
               Self.AlphaBlendValue := Self.AlphaBlendValue - 5;
         end;
      vk_Home: begin
            Self.AlphaBlendValue := 255;
         end;
      vk_End: begin
            Self.AlphaBlendValue := 50;
         end;
      vk_Escape: begin
            ActionUndo(actList, false);
            exit;
         end;
   else
         //정의되지 않은 키값일 경우 ....
      exit;
   end;

   //맵 영역을 넘어가는지 검사
   if (sg.ColCount <= newCol) or (newCol < 0) or
      (sg.ColCount <= nxtCol) or (nxtCol < 0) or
      (sg.RowCount <= newRow) or (newRow < 0) or
      (sg.RowCount <= nxtRow) or (nxtRow < 0) then exit;

   // 옮겨갈 위치의 값.
   newValue := sg.Cells[newCol, newRow];
   oldValue := sg.Cells[oldCol, oldRow];

   if tryStrToInt(newValue, imgIdx) then
   begin
      case imgIdx of
         0: begin // block
               exit;
            end;
         1: begin // home
               if SameText(oldValue, '5') then
                  oldValue := '1'
               else
                  oldValue := '';

               newValue := '5';
               nxtValue := '';
            end;
         2: begin // yes
               if SameText(oldValue, '5') then
                  oldValue := '1'
               else
                  oldValue := '';

               newValue := '5'; //man
            // next position 생성하고
            //이동할수 있는지 검사.
            //nxtValue  := '3'; //no
               if SameText(sg.Cells[nxtCol, nxtRow], EmptyStr) then
               begin
                  nxtValue := '3';
               end else
               begin
                  case strToint(sg.Cells[nxtCol, nxtRow]) of
                     0: exit;
                     1: nxtValue := '2';
                     2: exit;
                     3: exit;
                  else exit;
                  end;
               end;
            end;
         3: begin // no
               if SameText(oldValue, '5') then
                  oldValue := '1'
               else
                  oldValue := '';

               newValue := '4';
            // next position 생성하고
            //이동할수 있는지 검사.

               if SameText(sg.Cells[nxtCol, nxtRow], EmptyStr) then
               begin
                  nxtValue := '3';
               end else
               begin
                  case strToint(sg.Cells[nxtCol, nxtRow]) of
                     0: exit;
                     1: nxtValue := '2';
                     2: exit;
                     3: exit;
                  else exit;
                  end;
               end;
            end;
         4: begin // home
               exit;
            end;
      end;
   end else
   begin
      if SameText(sg.Cells[oldCol, oldRow], '5') then
      begin
         oldValue := '1';
         newValue := '4';
         nxtValue := '';
      end else
      begin
         oldValue := '';
         newValue := '4';
         nxtValue := '';
      end;
   end;

   //기존 값과 변경후 값을 기록한다.
   ActList.Lines.Add('begin');
   WriteLog(oldCol, oldRow, oldValue);
   sg.Cells[oldCol, oldRow] := oldValue;

   WriteLog(newCol, newRow, newValue);
   sg.Cells[newCol, newRow] := newValue;

   if not SameText(nxtValue, EmptyStr) then
   begin
      WriteLog(nxtCol, nxtRow, nxtValue);
      sg.Cells[nxtCol, nxtRow] := nxtValue;
   end;
   ActList.Lines.Add('end');

   if SameText(oldValue, EmptyStr) then
   begin
      dg.Canvas.Brush.Color := clWhite;
      dg.Canvas.FillRect(dg.CellRect(oldCol, oldRow));
   end else
   begin
      imgIdx := strToint(oldValue);
      iml.GetBitmap(imgIdx, bmp);
      dg.Canvas.BrushCopy(dg.CellRect(oldCol, oldRow), bmp, bmp.Canvas.ClipRect, clWhite);
   end;

   imgIdx := strToint(newValue);
   iml.GetBitmap(imgIdx, bmp);
   dg.Canvas.BrushCopy(dg.CellRect(newCol, newRow), bmp, bmp.Canvas.ClipRect, clWhite);

   if not SameText(nxtValue, EmptyStr) then
   begin
      imgIdx := strToint(nxtValue);
      iml.GetBitmap(imgIdx, bmp);
      dg.Canvas.BrushCopy(dg.CellRect(nxtCol, nxtRow), bmp, bmp.Canvas.ClipRect, clWhite);
   end;

   sg.Col := newCol;
   sg.Row := newRow;

   dg.Col := newCol;
   dg.Row := newRow;

   if map.SectionExists(FormatFloat('000', curLevel)) then
   begin
      if okCheck(sg) then
      begin
         curLevel := curLevel + 1;
         Caption := 'Push Push III - ' + intTostr(curLevel) + '단계';
         WriteCurrentLevel(map, curLevel);
         common.ResetMap(dg, sg, map, iml, curLevel);
         ActList.Lines.Clear;
         PlayList.Lines.Clear;
      end;
   end;
end;

procedure TfrmMain.WriteLog(ACol, ARow: integer; Value: string);
var
   History: string;
begin
   History := intToStr(ACol) + ';' + intTostr(ARow) + ';' + sg.Cells[ACol, ARow] + ';' + Value + ';';
   ActList.Lines.Add(History);
end;

procedure TfrmMain.ActionUndo(ActionHistory: TMemo; Auto: Boolean);
var
   i, j, p, idx: integer;
   oldValue, newValue, undoValue, tmp: string;
   Col, Row: integer;
begin
   for i := ActionHistory.Lines.Count - 1 downto 0 do
   begin
      tmp := trim(ActionHistory.Lines[i]);
      ActionHistory.Lines.Delete(i);

      if SameText(tmp, 'begin') then Break;
      if SameText(tmp, 'end') then Continue;

      p := Pos(';', tmp);
      j := 0;
      while p > 0 do
      begin
         if j = 0 then Col := strToint(copy(tmp, 1, p - 1));
         if j = 1 then Row := strToint(copy(tmp, 1, p - 1));
         if j = 2 then oldValue := copy(tmp, 1, p - 1);
         if j = 3 then newValue := copy(tmp, 1, p - 1);

         tmp := copy(tmp, p + 1, length(tmp));
         j := j + 1;
         p := Pos(';', tmp);
      end;

      if Auto then
      begin
         undoValue := newValue;
      end else
      begin
         undoValue := oldValue;
      end;

      sg.Cells[Col, Row] := undoValue;

      if TryStrToInt(undoValue, idx) then
      begin
         iml.GetBitmap(idx, bmp);
         Dg.Canvas.BrushCopy(dg.CellRect(Col, Row), bmp, bmp.Canvas.ClipRect, clWhite);
      end else
      begin
         dg.Brush.Color := clWhite;
         dg.Canvas.FillRect(dg.CellRect(Col, Row));
      end;

      dg.Col := Col;
      dg.Row := Row;

      sg.Col := Col;
      sg.Row := Row;

   end;
end;

procedure TfrmMain.Close1Click(Sender: TObject);
begin
   Self.Tag := 99;
   Close;
end;

procedure TfrmMain.ChangeIcon(newICON: HICON);
begin
   with IconData do
   begin
      cbSize := SizeOf(IconData);
      Wnd := Handle;
      uID := 100;
      uFlags := NIF_MESSAGE + NIF_ICON + NIF_TIP;
      uCallbackMessage := WM_USER + 1;
      hIcon := newICON;
      StrPCopy(szTip, Application.Title);
   end;

   Shell_NotifyIcon(NIM_MODIFY, @IconData);
end;

procedure TfrmMain.DeleteIcon;
begin
   try Shell_NotifyIcon(NIM_DELETE, @IconData); except end;
end;

procedure TfrmMain.ADDIcon(newICON: HICON);
begin
   with IconData do
   begin
      cbSize := SizeOf(IconData);
      Wnd := Handle;
      uID := 100;
      uFlags := NIF_MESSAGE + NIF_ICON + NIF_TIP;
      uCallbackMessage := WM_USER + 1;
      hIcon := newICON;
      StrPCopy(szTip, Application.Title);
   end;

   Shell_NotifyIcon(NIM_ADD, @IconData);
end;

procedure TfrmMain.PlayTimerTimer(Sender: TObject);
var
   i: integer;
   History, Step: string;
begin

   pl_msg.Tag := pl_msg.Tag + 1;
   Step := intTostr(playList.Lines.Count) + ' of ' + intTostr(ActList.Lines.Count);
   case pl_msg.Tag of
      0: pl_Msg.Caption := '▤▷▶▷▷▷▷▷▷▷▷▷▷▤ ' + Step;
      1: pl_Msg.Caption := '▧▷▷▶▷▷▷▷▷▷▷▷▷▧ ' + Step;
      2: pl_Msg.Caption := '▥▷▷▷▶▷▷▷▷▷▷▷▷▥ ' + Step;
      3: pl_Msg.Caption := '▨▷▷▷▷▶▷▷▷▷▷▷▷▨ ' + Step;
      4: pl_Msg.Caption := '▤▷▷▷▷▷▶▷▷▷▷▷▷▤ ' + Step;
      5: pl_Msg.Caption := '▧▷▷▷▷▷▷▶▷▷▷▷▷▧ ' + Step;
      6: pl_Msg.Caption := '▥▷▷▷▷▷▷▷▶▷▷▷▷▥ ' + Step;
      7: pl_Msg.Caption := '▨▷▷▷▷▷▷▷▷▶▷▷▷▨ ' + Step;
      8: pl_Msg.Caption := '▤▷▷▷▷▷▷▷▷▷▶▷▷▤ ' + Step;
      9: pl_Msg.Caption := '▧▷▷▷▷▷▷▷▷▷▷▶▷▧ ' + Step;
   else pl_msg.Tag := -1;
   end;

   if playList.Lines.Count = 0 then
   begin
      for i := ActList.Lines.Count downto 0 do
      begin
         playList.Lines.Add(ActList.Lines[i]);
      end;
      History := playList.Lines.Text;

      History := StringReplace(History, 'begin', '^', [rfReplaceAll, rfIgnoreCase]);
      History := StringReplace(History, 'end', 'begin', [rfReplaceAll, rfIgnoreCase]);
      History := StringReplace(History, '^', 'end', [rfReplaceAll, rfIgnoreCase]);

      playList.Lines.Text := trim(History);

      common.ResetMap(dg, sg, map, iml, curLevel);
   end;

   ActionUndo(playList, true);

   //플레이가 모두 끝나면 중지.
   TTimer(Sender).Enabled := (playList.Lines.Count > 0);
   if (playList.Lines.Count < 1) then pl_msg.Caption := '';
end;



procedure TfrmMain.Refresh1Click(Sender: TObject);
begin
   if not Self.Showing then
   begin
      idleTimer.Enabled := true;
      Self.Show;
   end;

   common.ResetMap(dg, sg, map, iml, curLevel);
   ActList.Lines.Clear;
   PlayList.Lines.Clear;
end;

procedure TfrmMain.ActionPlay1Click(Sender: TObject);
begin
   if not Self.Showing then
   begin
      idleTimer.Enabled := true;
      Self.Show;
   end;

   if self.ActList.Lines.Count < 1 then
   begin
      pl_msg.Caption := '읽어들인 Play List가 없습니다.';
      exit;
   end;

   playTimer.Enabled := not playTimer.Enabled;
   //재생이 중지되었으면
   if playTimer.Enabled then
   begin
      pl_msg.Caption := 'F4를 한번더 누르시면 Play가 중단됩니다.';
   end else
   begin
      pl_msg.Caption := 'F4를 누르시면 Action이 Play됩니다.';
      common.ResetMap(dg, sg, map, iml, curLevel);
      playList.Lines.Clear;
   end;
end;

procedure TfrmMain.SaveActionClick(Sender: TObject);
var
   fileName: string;
begin
   if not Self.Showing then
   begin
      idleTimer.Enabled := true;
      Self.Show;
   end;

   fileName := ExtractFilePath(paramstr(0));
   fileName := fileName + 'Action' + formatfloat('000', curLevel) + '.txt';
   if fileExists(fileName) then
   begin
      if ID_YES <> Application.MessageBox('기존파일을 삭제할까요?', 'Action Save...', MB_YESNO) then exit;
      DeleteFile(fileName);
   end;
   actList.Lines.SaveToFile(fileName);
   Self.pl_msg.Caption := fileName + '로 저장되었습니다.';
end;

procedure TfrmMain.LoadActionClick(Sender: TObject);
var
   fileName: string;
begin
   if not Self.Showing then
   begin
      idleTimer.Enabled := true;
      Self.Show;
   end;

   fileName := ExtractFilePath(paramstr(0));
   fileName := fileName + 'Action' + formatfloat('000', curLevel) + '.txt';
   if fileExists(fileName) then
   begin
      actList.Lines.LoadFromFile(fileName);
      pl_msg.Caption := 'Action을 읽었습니다. F4로 Play할 수 있습니다.';
   end else
   begin
      Self.pl_msg.Caption := fileName + '은 존재하지 않습니다.';
   end;
end;

procedure TfrmMain.Start1Click(Sender: TObject);
begin
   if not Self.Showing then
   begin
      idleTimer.Enabled := true;
      Self.Show;
   end;

   ChangeIcon(Self.Icon.Handle);
   Self.Left := Screen.Width - Self.Width;
   Self.Top := Screen.Height - (Self.Height + (Screen.Height - screen.WorkAreaHeight));

   if curLevel < 1 then curLevel := 1;
   common.ResetMap(dg, sg, map, iml, curLevel);
end;

procedure TfrmMain.levelMove(value: integer);
begin
   if not map.SectionExists(formatfloat('000', curLevel + value)) then
   begin
      pl_msg.Caption := ' 요청하신 ' + intTostr(curLevel + value) + '단계는 존재하지 않습니다.';
      exit;
   end;
   curLevel := curLevel + value;
   pl_msg.Caption := ' 현재는 ' + intTostr(curLevel) + '번째 단계입니다.';
   common.ResetMap(dg, sg, map, iml, curLevel);
end;

procedure TfrmMain.StayOnTop1Click(Sender: TObject);
begin
   if not Self.Showing then
   begin
      idleTimer.Enabled := true;
      Self.Show;
   end;

   if Self.FormStyle = fsStayOnTop then
   begin
      Self.StayOnTop1.Checked := false;
      Self.FormStyle := fsNormal;
   end else
   begin
      Self.StayOnTop1.Checked := true;
      Self.FormStyle := fsStayOnTop;
   end;

   DeleteIcon;
   ADDIcon(Self.Icon.Handle);
end;

procedure TfrmMain.HelpView;
begin
   mHelp.Left := 0;
   mHelp.Top := 0;

   mHelp.Visible := not mHelp.Visible;

   if mHelp.Visible then
   begin
      mHelp.Width := 400; //Self.ClientWidth;
      mHelp.Height := 450; //Self.ClientHeight;
      Self.ClientWidth := 400; //Width;
      Self.ClientHeight := 450; //Height;
   end else
   begin
      Self.ClientWidth := dg.Width;
      Self.ClientHeight := dg.Height;
   end;
   Self.Left := Screen.Width - Self.Width;
   Self.Top := Screen.Height - (Self.Height + (Screen.Height - screen.WorkAreaHeight));

   if not Self.Showing then
   begin
      idleTimer.Enabled := true;
      Self.Show;
   end;
end;

procedure TfrmMain.FormPaint(Sender: TObject);
begin
   DrawMap(dg, sg, iml);
end;

procedure TfrmMain.Help1Click(Sender: TObject);
begin
   HelpView;
end;

procedure TfrmMain.About1Click(Sender: TObject);
   function GetLocalTime(AFileTime: TFileTime): string;
   var
      SysTime: TSystemTime;
      FileTime: TFileTime;
      strDate, strTime: ShortString;
   begin
      filetimetolocalfiletime(AFileTime, FileTime);
      filetimetosystemtime(FileTime, SysTime);
      SetLength(strDate, GetDateFormat(LOCALE_USER_DEFAULT, 0, @SysTime, nil, @strDate[1], 255) - 1);
      SetLength(strTime, GetTimeFormat(LOCALE_USER_DEFAULT, TIME_NOSECONDS, @SysTime, nil, @strTime[1], 255) - 1);
      Result := strDate + '  ' + strTime;
   end;

var
   SHFinfo: TSHFileInfo;
   FindData: TWin32FindData;
   FindHandle: THandle;
   msg, fName, CDate, MDate: string;
begin
   fName := ExtractFileName(paramStr(0)); // 파일명(Name)

   FindHandle := Windows.FindFirstFile(PChar(fName), FindData);
   try
      CDate := GetLocalTime(FindData.ftCreationTime); // 파일생성일(Created)
      MDate := GetLocalTime(FindData.ftLastWriteTime); // 파일변경일(Modified)

      Msg := ' Push Push III ' + #13 + #13
         + '    생성일 : ' + CDate + #13
         + '    수정일 : ' + MDate + #13 + #13
         + ' Programming by nilriri™(nilriri2@hotmail.com)    ' + #13;

      MessageDlgTimer(Msg, mtInformation, [mbOK], 0, Self.Left, Self.Top, '', 5000);
   finally
      Windows.FindClose(FindHandle);
   end;
end;


procedure TfrmMain.mHelpDblClick(Sender: TObject);
begin
   HelpView;
   DrawMap(dg, sg, iml);
end;

procedure TfrmMain.mHelpKeyDown(Sender: TObject; var Key: Word;
   Shift: TShiftState);
begin
   if (key = vk_escape) or (key = vk_return) then
   begin
      HelpView;
      DrawMap(dg, sg, iml);
   end;
end;

procedure TfrmMain.MapDesigner1Click(Sender: TObject);
var
   SelfShow: Boolean;
begin
   SelfShow := Self.Showing;
   if SelfShow then Self.Hide;
   try
      frmMapDesigner := TfrmMapDesigner.create(Self);
      frmMapDesigner.AlphaBlend := Self.AlphaBlend;
      frmMapDesigner.AlphaBlendValue := Self.AlphaBlendValue;
      frmMapDesigner.showModal;
      frmMapDesigner.free;
   finally
      Refresh1Click(Self.Refresh1);
      if SelfShow then Self.Show;
   end;
end;

procedure TfrmMain.FormCloseQuery(Sender: TObject; var CanClose: Boolean);
begin
   // 플레이중에 화면이 최소화되거나 닫히면 재생중지.
   if playTimer.Enabled then Self.ActionPlay1Click(Self.ActionPlay1);

   if Self.Tag <> 99 then
   begin
      ChangeIcon(Application.Icon.Handle);
      Self.Hide;
      CanClose := false;
   end;

   if mHelp.Visible then
   begin
      HelpView;
      CanClose := false;
   end;
end;

procedure TfrmMain.idleTimerTimer(Sender: TObject);
begin
   if idleTimer.IdleMinutes >= idleTimer.MaxIdleMinutes then
   begin
      ChangeIcon(Application.Icon.Handle);
      Self.Hide;
      idleTimer.Enabled := false;
   end;
end;

end.

