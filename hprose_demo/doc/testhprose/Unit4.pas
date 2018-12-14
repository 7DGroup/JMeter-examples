unit Unit4;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, HproseHttpClient, HproseClient;

type
  TForm4 = class(TForm)
    Button1: TButton;
    HproseHttpClient1: THproseHttpClient;
    Button2: TButton;
    procedure Button1Click(Sender: TObject);
    procedure Button2Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form4: TForm4;

implementation

{$R *.dfm}

procedure TForm4.Button1Click(Sender: TObject);
begin
  HproseHttpClient1.UseService('http://10.0.0.100:9090/hprose_demo/Hello');
  ShowMessage(HproseHttpClient1.Invoke('sayHello', ['World中文']));
  ShowMessage(HproseHttpClient1.Invoke('add', [15,63]));
end;

//https://raw.githubusercontent.com/andot/hprose/master/doc/1.3/docx/pascal.docx
procedure TForm4.Button2Click(Sender: TObject);
begin
    HproseHttpClient1.UseService('http://10.0.0.105/hprose/http_server.php');
  ShowMessage(HproseHttpClient1.Invoke('hello', ['World时间']));
  ShowMessage(HproseHttpClient1.Invoke('asyncHello', ['xxx']));
end;

end.
