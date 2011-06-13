object frmPopup: TfrmPopup
  Left = 272
  Top = 320
  BorderStyle = bsSingle
  Caption = 'Map Importer...'
  ClientHeight = 98
  ClientWidth = 385
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -12
  Font.Name = #44404#47548#52404
  Font.Style = []
  OldCreateOrder = False
  Position = poOwnerFormCenter
  OnCreate = FormCreate
  PixelsPerInch = 96
  TextHeight = 12
  object Label1: TLabel
    Left = 36
    Top = 41
    Width = 222
    Height = 12
    Caption = 'Import '#54616#49892' '#45800#44228#47484' '#49440#53469#54616#50668' '#51452#49901#49884#50724'.'
  end
  object lblFileName: TLabel
    Left = 36
    Top = 17
    Width = 114
    Height = 12
    Caption = 'import '#54624' '#54028#51068#47749'...'
  end
  object cbLevel: TComboBox
    Left = 36
    Top = 63
    Width = 145
    Height = 20
    Style = csDropDownList
    ImeName = #54620#44397#50612' '#51077#47141' '#49884#49828#53596' (IME 2000)'
    ItemHeight = 12
    TabOrder = 0
  end
  object btnImport: TBitBtn
    Left = 204
    Top = 61
    Width = 75
    Height = 25
    Caption = 'Import'
    TabOrder = 1
    OnClick = btnImportClick
  end
  object btnCancel: TBitBtn
    Left = 288
    Top = 61
    Width = 75
    Height = 25
    Caption = 'Cancel'
    TabOrder = 2
    OnClick = btnCancelClick
  end
end
