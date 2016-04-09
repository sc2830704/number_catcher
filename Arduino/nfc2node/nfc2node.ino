//  DrawCard System with Android App in Mobile
//

#include "SPI.h"
#include "PN532_SPI.h"
#include "snep.h"
#include "NdefMessage.h"

#include "Wire.h"
#include "LiquidCrystal.h"
PN532_SPI pn532spi(SPI, 10);
SNEP nfc(pn532spi);
uint8_t ndefBuf[128];
String myNumber;
int waitingTime=0;
String currentNumber;
int preNumber=0;
int dSuss=0;
int sensorValue=0;
int prevValue=0;
boolean isGet=false;
String sRead;
String mynumber;
LiquidCrystal lcd(7,6,2,3,4,5);
//lcd 11 -> arduino 2
//lcd 12 -> arduino 3
//lcd 13 -> arduino 4
//lcd 14 -> arduino 5
//lcd 4 -> arduino 7
//lcd 6 -> arduino 6
//lcd 5, 1-> GND
//lcd 2 -> 5V
//lcd 2 -> 10K rese -> lcd3

void setup() {  
    Serial.begin(9600);   // set up the LCD's number of rows and columns: 
    lcd.begin(16, 2);
    
    lcd.setCursor(0,0);
    lcd.print("Awating No.:");    
  //  lcd.setCursor(0,1);
  //  lcd.print("Serving No.:");
  analogWrite(3,0);
}

void loop() {
      //等待使用者push資料
      Nfc_Receive();  
      //判斷資格符合，傳送抽卡資訊
      if(dSuss ==1)
      {
        while(!isGet){
          serialGet();//取得使用者號碼
          delay(200);//(重要!)等待serial完成接收資料
        }
        isGet=false;
        Nfc_Send();
      }
        
        
      delay(3000);
}

void Nfc_Send()    //send current information of waitting state
{
   
    NdefMessage message = NdefMessage();
    message.addTextRecord(mynumber);
    message.addTextRecord(currentNumber);
    
    int messageSize = message.getEncodedSize();
    if (messageSize > sizeof(ndefBuf)) {
        while (1) {
        }
    }
    message.encode(ndefBuf);
    if (0 >= nfc.write(ndefBuf, messageSize)) {
        String data =  "Awating No.:"+ (String)myNumber;    
        lcd.setCursor(0,0);
        lcd.print(data);
        dSuss=0;
    } else {
        String data =  "Awating No.:"+ (String)myNumber; 
        //myNumber=myNumber+1;
        lcd.setCursor(0,0);
        lcd.print(data);
        dSuss=0;
        //myNumber=0;
    }
  
}

void Nfc_Receive()  //receive the message from mobile
{
   
    
    String payloadAsString= "";
    int msgSize = nfc.read(ndefBuf, sizeof(ndefBuf));
    if (msgSize > 0) {
        NdefMessage msg  = NdefMessage(ndefBuf, msgSize);
     // msg.print();    
          NdefRecord record = msg.getRecord(0);
          

          int payloadLength = record.getPayloadLength();
          byte payload[payloadLength];
          record.getPayload(payload);        
        
          // The TNF and Type are used to determine how your application processes the payload
          // There's no generic processing for the payload, it's returned as a byte[]
          int startChar = 0;        
          
          startChar=getSkipCode(record);                 
          
          // Force the data into a String (might fail for some content)
          // Real code should use smarter processing
          for (int c = startChar; c < payloadLength; c++) {
            payloadAsString+= (char)payload[c];}
            
           
        
        serialSend(payloadAsString);
        // print on the LCD display
        
      //  lcd.setCursor(0,0);
      //  lcd.print(""+payloadAsString);
        dSuss=1;
    } else {
        dSuss=0;
    }
}

int getSkipCode(NdefRecord record)
{
  int startChar=0;
    if (record.getTnf() == TNF_WELL_KNOWN && record.getType()=="T") { 
            startChar = 0;}// text message skip the language code
    else if (record.getTnf() == TNF_WELL_KNOWN && record.getType()=="U") { 
            startChar = 1;// URI skip the url prefix (future versions should decode)
          }
          return startChar;
}
//**serialPort send data to nodejs server
//**payload[0] as ID
//**payload[1] as name
void serialSend(String payloadAsString)
{    
      Serial.print("{"); // begin character 
      Serial.print(payloadAsString);  
      Serial.print("}"); // end character    
  
}

//**get TotalNumber from NodeJS by serial port
void serialGet()
{
    while(Serial.available())
    {
      char input=(char)Serial.read();
      sRead += input;
      lcd.setCursor(0,1);
      lcd.print(input);
    }
    if(!Serial.available()&& sRead!="")
    {
         isGet=true;
         mynumber=sRead.substring(0,sRead.indexOf(";")); 
         currentNumber=sRead.substring(sRead.indexOf(";")+1,sRead.length()); 
         sRead="";
    }
     
}
