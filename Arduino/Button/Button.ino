
const int buttonPin = 2;   
const int ledPin = 13;      

int ledState = HIGH;         
int buttonState;           
int lastButtonState = LOW;   
String sRead="";
long lastDebounceTime = 0;  
long debounceDelay = 50;   

void setup() {
  Serial.begin(9600);   
  pinMode(buttonPin, INPUT);
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, ledState);
}

void loop() {
  int reading = digitalRead(buttonPin);
  buttonClick(reading);
  
}
void serialSend(String request)
{  
      Serial.print("B"); // begin character 
      Serial.print(request);  
      Serial.print("E"); // end character 
}
void buttonClick(int reading)
{
  if (reading != lastButtonState) {
    lastDebounceTime = millis();
  }

  if ((millis() - lastDebounceTime) > debounceDelay) {
      if (reading != buttonState) {
      buttonState = reading;
        if (buttonState == HIGH) {
          ledState = !ledState;
          serialSend("plus");
        }
      }
  }
  digitalWrite(ledPin, ledState);

  lastButtonState = reading;
}
