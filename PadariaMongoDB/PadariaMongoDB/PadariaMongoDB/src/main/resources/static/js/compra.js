// Carrinho (carrinho.html) e finalização da compra (compra.html)

// ---------- CARRINHO ----------
const areaItens = document.getElementById('itens');
if (areaItens) {
    function atualizarCarrinho() {
        const itens = Carrinho.itens();

        areaItens.innerHTML = itens.length === 0
            ? '<p>Seu carrinho está vazio.</p>'
            : itens.map((item, index) => `
                <div class="product-card">
                  <img src="${escaparHtml(item.img)}" alt="${escaparHtml(item.nome)}">
                  <div class="card-body">
                    <h3 class="product-name">${escaparHtml(item.nome)}</h3>
                    <span class="price">Preço unitário: ${formatarPreco(item.preco)}</span>
                    <span class="price">Subtotal: ${formatarPreco(item.preco * item.quantidade)}</span>
                    <div class="quantidade">
                      <button class="btn" onclick="alterarQuantidade(${index}, -1)">-</button>
                      <span>${item.quantidade}</span>
                      <button class="btn" onclick="alterarQuantidade(${index}, 1)">+</button>
                    </div>
                    <button class="btn btn-danger" onclick="removerCarrinho(${index})">Remover</button>
                  </div>
                </div>`).join('');

        document.getElementById('total').innerText = formatarPreco(Carrinho.total());
        document.getElementById('limparCarrinhoBtn').style.display = itens.length > 0 ? 'inline-block' : 'none';
    }

    window.alterarQuantidade = (index, delta) => {
        const itens = Carrinho.itens();
        itens[index].quantidade += delta;
        if (itens[index].quantidade <= 0) itens.splice(index, 1);
        Carrinho.salvar(itens);
        atualizarCarrinho();
    };

    window.removerCarrinho = (index) => {
        const itens = Carrinho.itens();
        itens.splice(index, 1);
        Carrinho.salvar(itens);
        atualizarCarrinho();
    };

    window.limparCarrinho = () => {
        Carrinho.limpar();
        atualizarCarrinho();
    };

    window.voltarProdutos = () => {
        location.href = 'produtos.html';
    };

    window.finalizarCompra = () => {
        if (Carrinho.itens().length === 0) {
            alert('Seu carrinho está vazio!');
            return;
        }
        location.href = Sessao.logado() ? 'compra.html' : 'usu.html?voltar=compra.html';
    };

    document.getElementById('ano').textContent = new Date().getFullYear();
    atualizarCarrinho();
}

// ---------- CHECKOUT ----------
const formCheckout = document.getElementById('checkoutForm');
if (formCheckout && exigirLogin()) {
    const usuario = Sessao.usuario();
    document.getElementById('nome').value = usuario.nome || '';
    document.getElementById('cpf').value = usuario.login || '';

    function renderResumo() {
        const resumo = document.getElementById('resumo');
        const itens = Carrinho.itens();
        resumo.innerHTML = itens.length === 0
            ? '<li>Seu carrinho está vazio.</li>'
            : itens.map(item =>
                `<li>${item.quantidade}x ${escaparHtml(item.nome)} - ${formatarPreco(item.preco * item.quantidade)}</li>`
            ).join('');
        document.getElementById('total').textContent = formatarPreco(Carrinho.total());
    }

    document.getElementById('btnVoltar').addEventListener('click', () => {
        location.href = 'carrinho.html';
    });

    formCheckout.addEventListener('submit', async (e) => {
        e.preventDefault();
        const itens = Carrinho.itens();
        if (itens.length === 0) {
            alert('Seu carrinho está vazio!');
            return;
        }

        // um id por unidade comprada
        const produtosIds = itens.flatMap(item => Array(item.quantidade).fill(item.id));
        const compra = {
            produtosIds,
            endereco: document.getElementById('endereco').value.trim(),
            cidade: document.getElementById('cidade').value.trim(),
            uf: document.getElementById('uf').value,
            formaPagamento: document.querySelector('input[name="pagamento"]:checked').value
        };

        try {
            const criada = await api('/compras', { method: 'POST', body: compra });
            Carrinho.limpar();
            alert(`Compra finalizada! Total: ${formatarPreco(criada.valorTotal)}. Obrigado por comprar na Padaria.`);
            location.href = 'produtos.html';
        } catch (erro) {
            if (erro.status === 401) {
                exigirLogin();
                return;
            }
            alert('Erro ao finalizar compra: ' + erro.message);
        }
    });

    renderResumo();
}
