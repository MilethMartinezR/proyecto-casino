const VALORES_NUMERICOS = {
  A: 11, '2': 2, '3': 3, '4': 4, '5': 5,
  '6': 6, '7': 7, '8': 8, '9': 9, '10': 10,
  J: 10, Q: 10, K: 10,
};

class Hand {
  constructor(cartas = []) {
    this.cartas = cartas;
  }

  agregar(carta) {
    return new Hand([...this.cartas, carta]);
  }

  calcularPuntos() {
    let total = 0;
    let ases = 0;
    for (const carta of this.cartas) {
      if (carta.valor === '?') continue;
      total += VALORES_NUMERICOS[carta.valor] ?? 0;
      if (carta.valor === 'A') ases++;
    }
    while (total > 21 && ases > 0) {
      total -= 10;
      ases--;
    }
    return total;
  }

  esBust() {
    return this.calcularPuntos() > 21;
  }

  esBlackjack() {
    return this.cartas.length === 2 && this.calcularPuntos() === 21;
  }

  ocultarPrimera() {
    const [, ...resto] = this.cartas;
    return new Hand([{ valor: '?', palo: '?' }, ...resto]);
  }

  toArray() {
    return [...this.cartas];
  }
}

module.exports = Hand;
