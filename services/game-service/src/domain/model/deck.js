const PALOS = ['♠', '♥', '♦', '♣'];
const VALORES = ['A', '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K'];

class Deck {
  constructor(cartas) {
    this.cartas = cartas;
  }

  static crear() {
    const cartas = [];
    for (const palo of PALOS) {
      for (const valor of VALORES) {
        cartas.push({ valor, palo });
      }
    }
    return new Deck(cartas);
  }

  mezclar() {
    const c = [...this.cartas];
    for (let i = c.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [c[i], c[j]] = [c[j], c[i]];
    }
    return new Deck(c);
  }

  sacarCarta() {
    if (this.cartas.length === 0) throw new Error('Mazo vacío');
    const [carta, ...resto] = this.cartas;
    return { carta, deck: new Deck(resto) };
  }

  toArray() {
    return [...this.cartas];
  }
}

module.exports = Deck;
