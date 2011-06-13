    unit myDialogs;

{$R-,T-,H+,X+}

interface

uses Windows, Messages, SysUtils, CommDlg, Classes, Graphics, Controls,
  Forms, StdCtrls, Printers, ExtCtrls, Dialogs, Comctrls ;

   function CreateMessageDialog(const Msg : String; Title: String = ''; DlgType: TMsgDlgType=mtInformation;
     Buttons: TMsgDlgButtons=[mbOk]; Sec : integer = 0): TForm;

   function MessageDlgTimer(const Msg : String; DlgType: TMsgDlgType=mtInformation;
     Buttons: TMsgDlgButtons=[mbOk]; HelpCtx: Longint=0; X:integer = -1; Y: Integer=-1;
     const HelpFileName: string=''; Sec : integer=5000): Integer; overload; 

   function MessageDlgTimer(const Msg : String; Title: String; DlgType: TMsgDlgType=mtInformation;
     Buttons: TMsgDlgButtons=[mbOk]; HelpCtx: Longint=0; X:integer = -1; Y: Integer=-1;
     const HelpFileName: string=''; Sec : integer=5000): Integer; overload; 

implementation

uses
  Consts, Dlgs, Math;

{ Message dialog }

function GetAveCharSize(Canvas: TCanvas): TPoint;
var
  I: Integer;
  Buffer: array[0..51] of Char;
begin
  for I := 0 to 25 do Buffer[I] := Chr(I + Ord('A'));
  for I := 0 to 25 do Buffer[I + 26] := Chr(I + Ord('a'));
  GetTextExtentPoint(Canvas.Handle, Buffer, 52, TSize(Result));
  Result.X := Result.X div 52;
end;

type
  TMessageForm = class(TForm)
  private
    Timer : TTimer;
    Timer2 : TTimer;
    ProgressBar : TProgressBar;
    Msg: TLabel;
    procedure HelpButtonClick(Sender: TObject);
    procedure TimerOnTimer(Sender: TObject);
    procedure Timer2OnTimer(Sender: TObject);
  protected
    procedure CustomKeyDown(Sender: TObject; var Key: Word; Shift: TShiftState);
    procedure WriteToClipBoard(Text: String);
    function GetFormText: String;
  public
    constructor CreateNew(AOwner: TComponent; Sec : integer = 0); reintroduce;
    destructor Destroy; reintroduce;
  end;

constructor TMessageForm.CreateNew(AOwner: TComponent; Sec : integer = 0);
var
  NonClientMetrics: TNonClientMetrics;
begin
  inherited CreateNew(AOwner);
  NonClientMetrics.cbSize := sizeof(NonClientMetrics);
  if SystemParametersInfo(SPI_GETNONCLIENTMETRICS, 0, @NonClientMetrics, 0) then
    Font.Handle := CreateFontIndirect(NonClientMetrics.lfMessageFont);
    
  ProgressBar := TProgressBar.Create(Self);
  with ProgressBar do
  begin
    Name := 'ProgressBar';
    Parent := Self;
    Smooth := True;
    BorderStyle := bsNone;
    Max := Sec div 1000 * 2;
    Visible := false;
  end;
  
  Timer := TTimer.Create(Self);
  Timer.Interval := Sec;
  Timer.OnTimer := TimerOnTimer;
  Timer.Enabled := True;

  Timer2 := TTimer.Create(Self);
  Timer2.Interval := 500;
  Timer2.OnTimer := Timer2OnTimer;
  Timer2.Enabled := True;
end;

destructor TMessageForm.Destroy;
begin
  Timer.Free;
  inherited Destroy;
end;

procedure TMessageForm.TimerOnTimer(Sender: TObject);
begin
   Self.ModalResult := mrCancel;
end;

procedure TMessageForm.Timer2OnTimer(Sender: TObject);
begin
   if Not ProgressBar.Visible then  
   begin
      ProgressBar.Visible := true; 
      ProgressBar.SetBounds(
         Msg.Left - 5, 
         Msg.Top + 
         Msg.Height + 10, 
         ClientWidth - (Msg.Left + 5), 
         7);
   end;
   ProgressBar.Position := ProgressBar.Position + 1;
end;

procedure TMessageForm.HelpButtonClick(Sender: TObject);
begin
  Application.HelpContext(HelpContext);
end;

procedure TMessageForm.CustomKeyDown(Sender: TObject; var Key: Word; Shift: TShiftState);
begin
  if (Shift = [ssCtrl]) and (Key = Word('C')) then
  begin
    Beep;
    WriteToClipBoard(GetFormText);
  end;
end;

procedure TMessageForm.WriteToClipBoard(Text: String);
var
  Data: THandle;
  DataPtr: Pointer;
begin
  if OpenClipBoard(0) then
  begin
    try
      Data := GlobalAlloc(GMEM_MOVEABLE+GMEM_DDESHARE, Length(Text) + 1);
      try
        DataPtr := GlobalLock(Data);
        try
          Move(PChar(Text)^, DataPtr^, Length(Text) + 1);
          EmptyClipBoard;
          SetClipboardData(CF_TEXT, Data);
        finally
          GlobalUnlock(Data);
        end;
      except
        GlobalFree(Data);
        raise;
      end;
    finally
      CloseClipBoard;
    end;
  end
  else
    raise Exception.CreateRes(@SCannotOpenClipboard);
end;

function TMessageForm.GetFormText: String;
var
  DividerLine, ButtonCaptions: string;
  I: integer;
begin
  DividerLine := StringOfChar('-', 27) + sLineBreak;
  for I := 0 to ComponentCount - 1 do
    if Components[I] is TButton then
      ButtonCaptions := ButtonCaptions + TButton(Components[I]).Caption +
        StringOfChar(' ', 3);
  ButtonCaptions := StringReplace(ButtonCaptions,'&','', [rfReplaceAll]);
  Result := Format('%s%s%s%s%s%s%s%s%s%s', [DividerLine, Caption, sLineBreak,
    DividerLine, Msg.Caption, sLineBreak, DividerLine, ButtonCaptions,
    sLineBreak, DividerLine]);
end;

var
  Captions: array[TMsgDlgType] of Pointer = (@SMsgDlgWarning, @SMsgDlgError,
    @SMsgDlgInformation, @SMsgDlgConfirm, nil);
  IconIDs: array[TMsgDlgType] of PChar = (IDI_EXCLAMATION, IDI_HAND,
    IDI_ASTERISK, IDI_QUESTION, nil);
  ButtonNames: array[TMsgDlgBtn] of string = (
    'Yes', 'No', 'OK', 'Cancel', 'Abort', 'Retry', 'Ignore', 'All', 'NoToAll',
    'YesToAll', 'Help');
  ButtonCaptions: array[TMsgDlgBtn] of Pointer = (
    @SMsgDlgYes, @SMsgDlgNo, @SMsgDlgOK, @SMsgDlgCancel, @SMsgDlgAbort,
    @SMsgDlgRetry, @SMsgDlgIgnore, @SMsgDlgAll, @SMsgDlgNoToAll, @SMsgDlgYesToAll,
    @SMsgDlgHelp);
  ModalResults: array[TMsgDlgBtn] of Integer = (
    mrYes, mrNo, mrOk, mrCancel, mrAbort, mrRetry, mrIgnore, mrAll, mrNoToAll,
    mrYesToAll, 0);
var
  ButtonWidths : array[TMsgDlgBtn] of integer;  // initialized to zero

function CreateMessageDialog(const Msg : String; Title: String = ''; DlgType: TMsgDlgType=mtInformation;
     Buttons: TMsgDlgButtons=[mbOk]; Sec : integer = 0): TForm;
const
  mcHorzMargin = 8;
  mcVertMargin = 8;
  mcHorzSpacing = 10;
  mcVertSpacing = 10;
  mcButtonWidth = 50;
  mcButtonHeight = 14;
  mcButtonSpacing = 4;
var
  DialogUnits: TPoint;
  HorzMargin, VertMargin, HorzSpacing, VertSpacing, ButtonWidth,
  ButtonHeight, ButtonSpacing, ButtonCount, ButtonGroupWidth,
  IconTextWidth, IconTextHeight, X, ALeft: Integer;
  B, DefaultButton, CancelButton: TMsgDlgBtn;
  IconID: PChar;
  TextRect: TRect;
begin
  Result := TMessageForm.CreateNew(Application, Sec);
  with Result do
  begin
    BiDiMode := Application.BiDiMode;
    BorderStyle := bsDialog;
    Canvas.Font := Font;
    KeyPreview := True;
    OnKeyDown := TMessageForm(Result).CustomKeyDown;
    DialogUnits := GetAveCharSize(Canvas);
    HorzMargin := MulDiv(mcHorzMargin, DialogUnits.X, 4);
    VertMargin := MulDiv(mcVertMargin, DialogUnits.Y, 8);
    HorzSpacing := MulDiv(mcHorzSpacing, DialogUnits.X, 4);
    VertSpacing := MulDiv(mcVertSpacing, DialogUnits.Y, 8);
    ButtonWidth := MulDiv(mcButtonWidth, DialogUnits.X, 4);
    for B := Low(TMsgDlgBtn) to High(TMsgDlgBtn) do
    begin
      if B in Buttons then
      begin
        if ButtonWidths[B] = 0 then
        begin
          TextRect := Rect(0,0,0,0);
          Windows.DrawText( canvas.handle,
            PChar(LoadResString(ButtonCaptions[B])), -1,
            TextRect, DT_CALCRECT or DT_LEFT or DT_SINGLELINE or
            DrawTextBiDiModeFlagsReadingOnly);
          with TextRect do ButtonWidths[B] := Right - Left + 8;
        end;
        if ButtonWidths[B] > ButtonWidth then
          ButtonWidth := ButtonWidths[B];
      end;
    end;
    ButtonHeight := MulDiv(mcButtonHeight, DialogUnits.Y, 8);
    ButtonSpacing := MulDiv(mcButtonSpacing, DialogUnits.X, 4);
    SetRect(TextRect, 0, 0, Screen.Width div 2, 0);
    DrawText(Canvas.Handle, PChar(Msg), Length(Msg)+1, TextRect,
      DT_EXPANDTABS or DT_CALCRECT or DT_WORDBREAK or
      DrawTextBiDiModeFlagsReadingOnly);
    IconID := IconIDs[DlgType];
    IconTextWidth := TextRect.Right;
    IconTextHeight := TextRect.Bottom;
    if IconID <> nil then
    begin
      Inc(IconTextWidth, 32 + HorzSpacing);
      if IconTextHeight < 32 then IconTextHeight := 32;
    end;
    ButtonCount := 0;
    for B := Low(TMsgDlgBtn) to High(TMsgDlgBtn) do
      if B in Buttons then Inc(ButtonCount);
    ButtonGroupWidth := 0;
    if ButtonCount <> 0 then
      ButtonGroupWidth := ButtonWidth * ButtonCount +
        ButtonSpacing * (ButtonCount - 1);
    ClientWidth := Max(IconTextWidth, ButtonGroupWidth) + HorzMargin * 2;
    ClientHeight := IconTextHeight + ButtonHeight + VertSpacing +
      VertMargin * 2;
    Left := (Screen.Width div 2) - (Width div 2);
    Top := (Screen.Height div 2) - (Height div 2);

    if SameText(Title, EmptyStr) then
    begin
      if DlgType <> mtCustom then
        Caption := LoadResString(Captions[DlgType]) else
        Caption := Application.Title;
      end else
    begin
      Caption := Title;
    end;

    if IconID <> nil then
      with TImage.Create(Result) do
      begin
        Name := 'Image';
        Parent := Result;
        Picture.Icon.Handle := LoadIcon(0, IconID);
        SetBounds(HorzMargin, VertMargin, 32, 32);
      end;
      
    TMessageForm(Result).Msg := TLabel.Create(Result);
    with TMessageForm(Result).Msg do
    begin
      Name := 'Message';
      Parent := Result;
      WordWrap := True;
      Caption := Msg;
      BoundsRect := TextRect;
      BiDiMode := Result.BiDiMode;
      ALeft := IconTextWidth - TextRect.Right + HorzMargin;
      if UseRightToLeftAlignment then
        ALeft := Result.ClientWidth - ALeft - Width;
      SetBounds(ALeft, VertMargin,
        TextRect.Right, TextRect.Bottom);
    end;
    
    if mbOk in Buttons then DefaultButton := mbOk else
      if mbYes in Buttons then DefaultButton := mbYes else
        DefaultButton := mbRetry;
    if mbCancel in Buttons then CancelButton := mbCancel else
      if mbNo in Buttons then CancelButton := mbNo else
        CancelButton := mbOk;
    X := (ClientWidth - ButtonGroupWidth) div 2;
    for B := Low(TMsgDlgBtn) to High(TMsgDlgBtn) do
      if B in Buttons then
        with TButton.Create(Result) do
        begin
          Name := ButtonNames[B];
          Parent := Result;
          Caption := LoadResString(ButtonCaptions[B]);
          ModalResult := ModalResults[B];
          if B = DefaultButton then Default := True;
          if B = CancelButton then Cancel := True;
          SetBounds(X, IconTextHeight + VertMargin + VertSpacing,
            ButtonWidth, ButtonHeight);
          Inc(X, ButtonWidth + ButtonSpacing);
          if B = mbHelp then
            OnClick := TMessageForm(Result).HelpButtonClick;
        end;
  end;
end;

function MessageDlgTimer(const Msg : String; DlgType: TMsgDlgType=mtInformation;
     Buttons: TMsgDlgButtons=[mbOk]; HelpCtx: Longint=0; X:integer = -1; Y: Integer=-1;
     const HelpFileName: string=''; Sec : integer=5000): Integer; overload;
begin
  Result := MessageDlgTimer(Msg,'', DlgType, Buttons,HelpCtx,x,y,HelpFileName,Sec);
end;

function MessageDlgTimer(const Msg : String; Title: String; DlgType: TMsgDlgType=mtInformation;
     Buttons: TMsgDlgButtons=[mbOk]; HelpCtx: Longint=0; X:integer = -1; Y: Integer=-1;
     const HelpFileName: string=''; Sec : integer=5000): Integer; Overload;
begin
  with CreateMessageDialog(Msg, Title, DlgType, Buttons, Sec) do
    try
      HelpContext := HelpCtx;
      HelpFile := HelpFileName;
      if X >= 0 then Left := X;
      if Y >= 0 then Top := Y;
      if (Y < 0) and (X < 0) then Position := poScreenCenter;
      Result := ShowModal;
    finally
      Free;
    end;
end;

end.

