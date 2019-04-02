object Form4: TForm4
  Left = 0
  Top = 0
  Caption = 'test hprose'
  ClientHeight = 300
  ClientWidth = 635
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'Tahoma'
  Font.Style = []
  OldCreateOrder = False
  PixelsPerInch = 96
  TextHeight = 13
  object Button1: TButton
    Left = 88
    Top = 56
    Width = 121
    Height = 25
    Caption = 'java hprose server'
    TabOrder = 0
    OnClick = Button1Click
  end
  object Button2: TButton
    Left = 232
    Top = 56
    Width = 129
    Height = 25
    Caption = 'php hprose server'
    TabOrder = 1
    OnClick = Button2Click
  end
  object HproseHttpClient1: THproseHttpClient
    KeepAlive = True
    KeepAliveTimeout = 300
    ProxyPort = 8080
    UserAgent = 'Hprose Http Client for Delphi (Synapse)'
    Timeout = 30000
    Left = 208
    Top = 160
  end
end
