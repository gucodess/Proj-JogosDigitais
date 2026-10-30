/*
 * Breakout Rehab - leitura do potenciometro (Arduino Uno)
 *
 * Ligacao do potenciometro de 10k:
 *   - terminal ESQUERDO -> 5V
 *   - terminal CENTRAL (cursor) -> A0
 *   - terminal DIREITO -> GND
 *
 * O Arduino le o valor analogico (0..1023) e o envia pela porta serial (USB)
 * como texto, uma leitura por linha, a 115200 baud. O jogo (SerialController,
 * via jSerialComm) le essas linhas e move a raquete.
 *
 * Se o movimento ficar invertido, troque 5V <-> GND nas pontas do potenciometro.
 */

const int PINO_POT = A0;

void setup() {
  Serial.begin(115200); // precisa casar com o BAUD do SerialController no jogo
}

void loop() {
  int valor = analogRead(PINO_POT); // 0..1023
  Serial.println(valor);            // envia o valor + '\n'
  delay(20);                        // ~50 leituras por segundo
}
