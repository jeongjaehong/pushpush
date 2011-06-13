unit VIdle;

{

   vidle.pas
   
   Enabled        : Timer의 활성화 비활성화 지정.
   MaxIdleMinutes : OnTimer이벤트에서 사용할 최대 유휴시간정보
   Interval       : OnTimer이벤트가 발생할 밀리 세컨드 단위 숫자
   Name           : Timer를 컨트롤을 제어하기위한 이름.
   Tag            : tag값.
   
   History:
        2003.12.18 : 
                SetEnabled 프로시져에 GReset 프로시져 호출부분 추가.
                지정된 idle Time 발생후 복귀하고자할때 기존 idle 타임이 그대로 남아있어서
                timer를 활성화시킬때 바로 idle이벤트가 발생하기때문에..추가.

   사용예 :

        procedure TfrmMain.idleTimerTimer(Sender: TObject);
        begin
           if idleTimer.IdleMinutes >= idleTimer.MaxIdleMinutes then
           begin
              idleTimer.Enabled := false;
              // 지정한 idel time에 도달했을때 실행할 작업...
           end;
        end;
     

}

interface

uses
	Windows, Messages, SysUtils, Classes, Graphics, Controls, Forms, Dialogs,
	ExtCtrls;

type
	TVIdleTimer = class(TTimer)
	private
		{ Private-Deklarationen }
		MaxIdle : integer;
		FEnabled: Boolean;
		function GetMaxIdleMinutes: integer;
		procedure SetMaxIdleMinutes(Value: Integer);
		procedure SetEnabled(Value: Boolean);
		function GetLastActivity: TDateTime;
		procedure SetLastActivity(const t: TDateTime);
		function GetIdleTime: TDateTime;
		function GetIdleMinutes: integer;
	protected
		{ Protected-Deklarationen }
	public
		{ Public-Deklarationen }
		constructor Create(AOwner: TComponent);override;
		destructor Destroy;override;
		property LastActivity: TDateTime read GetLastActivity write SetLastActivity;
			// last user activity.
		property IdleTime: TDateTime read GetIdleTime;
			// time passed since last user activity
		property IdleMinutes: Integer read GetIdleMinutes;
			// minutes passed since last user activity
	published
		{ Published-Deklarationen }
		property Enabled: Boolean read FEnabled write SetEnabled default True;
		property MaxIdleMinutes: Integer read GetMaxIdleMinutes  write SetMaxIdleMinutes default 1;
			// Enabled is the same as TTimer.Enabled, but it also controls
			// monitoring user activity for ALL allocated TVIdleTimers. Use this
			// for temporary interruptions of monitoring.
	end;

procedure Register;

implementation

var
	GLastActivity: TDateTime;
	GInstanceCount: Integer;
	whKeyboard,whMouse: HHook;

procedure GReset;
begin
	GLastActivity := Now;
end;

{$IFDEF WIN32}
function MouseHookCallBack(Code: integer; Msg: WPARAM; MouseHook: LPARAM): LRESULT; stdcall;
{$ELSE}
function MouseHookCallBack(Code: integer; Msg: word; MouseHook: longint): longint; export;
{$ENDIF}
begin
	if Code >= 0 then GReset;
	Result := CallNextHookEx(whMouse,Code,Msg,MouseHook);
end;

{$IFDEF WIN32}
function KeyboardHookCallBack(Code: integer; Msg: WPARAM; KeyboardHook: LPARAM): LRESULT; stdcall;
{$ELSE}
function KeyboardHookCallBack(Code: integer; Msg: word; KeyboardHook: longint): longint; export;
{$ENDIF}
begin
	if Code >= 0 then GReset;
	Result := CallNextHookEx(whKeyboard,Code,Msg,KeyboardHook);
end;

function HookActive: Boolean;
begin
	Result := whKeyboard <> 0;
end;

procedure CreateHooks;
	function GetModuleHandleFromInstance: THandle;
	var
		s: array[0..512] of char;
	begin
		GetModuleFileName(hInstance,s,sizeof(s)-1);
		Result := GetModuleHandle(s);
	end;
begin
	if not HookActive then begin
		whMouse := SetWindowsHookEx(WH_MOUSE,MouseHookCallBack,
																GetModuleHandleFromInstance,
																{$IFDEF WIN32}GetCurrentThreadID{$ELSE}GetCurrentTask{$ENDIF});
		whKeyboard := SetWindowsHookEx(WH_KEYBOARD,KeyboardHookCallBack,
																	 GetModuleHandleFromInstance,
																	 {$IFDEF WIN32}GetCurrentThreadID{$ELSE}GetCurrentTask{$ENDIF});
	end;
end;

procedure RemoveHooks;
begin
	if HookActive then
		try
			UnhookWindowsHookEx(whKeyboard);
			UnhookWindowsHookEx(whMouse);
		finally
			whKeyboard := 0;
			whMouse := 0;
		end;
end;

constructor TVIdleTimer.Create;
begin
	inherited;
	CreateHooks;
	if GInstanceCount = 0 then GReset;
	MaxIdle := 1;
	Inc(GInstanceCount);
end;

destructor TVIdleTimer.Destroy;
begin
	Dec(GInstanceCount);
	if GInstanceCount <= 0 then RemoveHooks;
	inherited;
end;

procedure TVIdleTimer.SetMaxIdleMinutes(Value: Integer);
begin
	MaxIdle := Value;
end;

function TVIdleTimer.GetMaxIdleMinutes : Integer;
begin
	GetMaxIdleMinutes := MaxIdle;
end;

procedure TVIdleTimer.SetEnabled(Value: Boolean);
begin
	FEnabled := Value;

   GReset; //enable True False가 바뀔때 최종 활성시간을 리셋해준다.
   
	inherited Enabled := Value;
	if FEnabled then
		CreateHooks
	else
		RemoveHooks;
end;

function TVIdleTimer.GetLastActivity: TDateTime;
begin
	Result := GLastActivity;
end;

procedure TVIdleTimer.SetLastActivity(const t: TDateTime);
begin
	GLastActivity := t;
end;

function TVIdleTimer.GetIdleTime: TDateTime;
begin
	Result := Now - GLastActivity;
end;

function TVIdleTimer.GetIdleMinutes: Integer;
begin
	Result := Trunc(IdleTime*1440.1);
end;

procedure Register;
begin
	RegisterComponents('Verisoft', [TVIdleTimer]);
end;

initialization
	GInstanceCount := 0;
	whKeyboard := 0;
	whMouse := 0;
end.