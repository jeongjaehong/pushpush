unit common;

interface
uses
   Grids, Graphics, INIFiles, Classes, SysUtils, Controls, myDialogs;

procedure DrawGridClear(dg: TDrawGrid);
procedure StringGridClear(sg: TStringGrid);

procedure LoadMap(sg: TStringGrid; mapData: string; row: integer);
procedure DrawMap(dg: TDrawGrid; sg: TStringGrid; iml: TImageList);
procedure ResetMap(dg: TDrawGrid; sg: TStringGrid; map: TINIFile; iml: TImageList; level: integer);
procedure MapWrite(sg: TStringGrid; map: TINIFile; level: integer);
procedure WriteCurrentLevel(map: TINIFile; level: integer);

function ReadCurrentLevel(map: TINIFile): integer;
function getMaxLevel(map: TIniFile): integer;
function okCheck(sg: TStringGrid): Boolean;

implementation

function okCheck(sg: TStringGrid): Boolean;
var
   i, j: integer;
begin
   Result := true;
   for i := 0 to sg.ColCount - 1 do
   begin
      for j := 0 to sg.RowCount - 1 do
      begin
         if SameText(sg.Cells[i, j], '3') then
         begin
            Result := false;
            exit;
         end;
      end;
   end;
end;

procedure MapWrite(sg: TStringGrid; map: TINIFile; level: integer);
var
   Section, ident, value: string;
   i, j, rowCnt: integer;
begin
   rowCnt := 0;
   with map do
   begin
      Section := formatfloat('000', level);
      for i := 0 to SG.RowCount - 1 do
      begin
         ident := 'line' + formatfloat('00', i + 1);

         value := '';
         for j := 0 to SG.ColCount - 1 do
         begin
            if SameText(SG.Cells[j, i], EmptyStr) then
            begin
               value := value + '9';
            end else
            begin
               value := value + SG.Cells[j, i];
            end;
         end;

         WriteString(Section, ident, value);
         rowCnt := rowCnt + 1;
      end;
      WriteInteger(Section, 'line00', rowCnt);
   end;
end;

procedure WriteCurrentLevel(map: TINIFile; level: integer);
begin
   map.WriteInteger('000', 'level', level);
end;

function ReadCurrentLevel(map: TINIFile): integer;
begin
   Result := map.ReadInteger('000', 'level', 1);
end;

procedure ResetMap(dg: TDrawGrid; sg: TStringGrid; map: TINIFile; iml: TImageList; level: integer);
var
   tot, i: integer;
   section, ident, mapData: string;
const
   identPreFix: string = 'line';
begin
   try
      DrawGridClear(dg);
      StringGridClear(sg);

      section := formatfloat('000', level);

      if not map.SectionExists(section) then exit;

      tot := map.ReadInteger(section, 'line00', 0);
      for i := 0 to tot - 1 do
      begin
         ident := identPreFix + formatfloat('00', i + 1);
         mapData := map.ReadString(section, ident, '');
         LoadMap(sg, mapData, i);
      end;
      DrawMap(dg, sg, iml);
   except
      on e: exception do
      begin
         myDialogs.MessageDlgTimer(e.message);
      end;
   end;
end;

procedure LoadMap(sg: TStringGrid; mapData: string; row: integer);
var
   i: integer;
   Data: string;
begin
   Data := Trim(mapData);
   for i := 0 to Length(Data) do
   begin
      if mapData[i] in ['0'..'5'] then sg.Cells[i - 1, row] := mapData[i];
   end;
end;

procedure DrawMap(dg: TDrawGrid; sg: TStringGrid; iml: TImageList);
var
   col, row, idx: integer;
   tmpBMP: TBitmap;
   posC, posR: integer;
begin
   tmpBMP := TBitmap.Create;
   posC := 0;
   posR := 0;
   try
      for col := 0 to sg.ColCount - 1 do
      begin
         for row := 0 to sg.RowCount - 1 do
         begin
            if SameText(sg.Cells[col, row], EmptyStr) then
            begin
            end else
               dg.Canvas.Brush.Color := clWhite;
            dg.Canvas.FillRect(dg.CellRect(col, row));
            begin
               if trystrtoint(sg.Cells[col, row], idx) then
               begin
                  iml.GetBitmap(idx, tmpBMP);
                  dg.Canvas.BrushCopy(dg.CellRect(col, row), tmpBMP, tmpBMP.Canvas.ClipRect, clWhite);
                  sg.Cells[col, row] := intTostr(idx);
                  if idx = 4 then
                  begin
                     posC := col;
                     posR := row;
                  end;
               end;
            end;
         end;
      end;
   finally
      tmpBMP.Free;
      try
         sg.Col := posC;
         sg.Row := posR;
         dg.Col := posC;
         dg.Row := posR;
      except
      end;
   end;
end;

function getMaxLevel(map: TIniFile): integer;
var
   Section: TStrings;
begin
   Section := TStringList.Create;
   try
      map.ReadSections(Section);
      Result := Section.Count;
   except
      Result := 0;
      Section.Free;
   end;
end;

procedure DrawGridClear(dg: TDrawGrid);
var
   i, j: integer;
   col, row: integer;
begin
   col := dg.col;
   row := dg.row;
   try
      for i := 0 to dg.ColCount - 1 do
      begin
         for j := 0 to dg.RowCount - 1 do
         begin
            dg.Canvas.Brush.Color := dg.Color;
            dg.Canvas.FillRect(dg.CellRect(i, j));
         end;
      end;
   finally
      dg.Col := col;
      dg.Row := row;
   end;
end;

procedure StringGridClear(sg: TStringGrid);
var
   i: integer;
   col, row: integer;
begin
   col := sg.col;
   row := sg.row;
   try
      for i := 0 to sg.RowCount - 1 do sg.Rows[i].Clear;
   finally
      sg.Col := col;
      sg.Row := row;
   end;
end;

end.

