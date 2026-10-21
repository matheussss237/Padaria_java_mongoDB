// Página de produtos: carrega o catálogo da API, filtra e adiciona ao carrinho

const listaProdutos = document.getElementById('listaProdutos');
const buscar = document.getElementById('buscar');
const filtro = document.getElementById('filtro');
let produtos = [];

function atualizarMenu() {
    const usuario = Sessao.usuario();
    document.getElementById('qtdCarrinho').textContent = Carrinho.quantidade();
    document.getElementById('menuConta').style.display = usuario ? '' : 'none';
    document.getElementById('menuSair').style.display = usuario ? '' : 'none';
    document.getElementById('menuEntrar').style.display = usuario ? 'none' : '';
    document.getElementById('saudacao').textContent = usuario ? `Olá, ${usuario.nome}!` : '';
}

function renderizar() {
    const q = buscar.value.trim().toLowerCase();
    const categoria = filtro.value;

    const visiveis = produtos.filter(produto => {
        const texto = `${produto.nome} ${produto.descricao || ''}`.toLowerCase();
        return (q === '' || texto.includes(q)) && (categoria === 'all' || produto.categoria === categoria);
    });

    if (visiveis.length === 0) {
        listaProdutos.innerHTML = '<li class="vazio">Nenhum produto encontrado.</li>';
        return;
    }

    listaProdutos.innerHTML = visiveis.map(produto => `
        <li class="product-card" data-category="${escaparHtml(produto.categoria)}">
          <img src="${escaparHtml(produto.imagem || 'img/pao&sabor.png')}" alt="${escaparHtml(produto.nome)}">
          <div class="card-body">
            <h3 class="product-name">${escaparHtml(produto.nome)}</h3>
            <p class="product-desc">${escaparHtml(produto.descricao)}</p>
            <div class="card-meta">
              <span class="price">${formatarPreco(produto.preco)}</span>
              <button class="btn btn-primary" data-id="${escaparHtml(String(produto.id))}"
                      aria-label="Adicionar ${escaparHtml(produto.nome)} ao carrinho">Adicionar</button>
            </div>
          </div>
        </li>`).join('');
}

listaProdutos.addEventListener('click', (e) => {
    const botao = e.target.closest('button[data-id]');
    if (!botao) return;
    const produto = produtos.find(p => String(p.id) === botao.dataset.id);
    Carrinho.adicionar(produto);
    atualizarMenu();
    botao.textContent = 'Adicionado ✓';
    setTimeout(() => { botao.textContent = 'Adicionar'; }, 1200);
});

buscar.addEventListener('input', renderizar);
filtro.addEventListener('change', renderizar);

async function carregarProdutos() {
    try {
        produtos = await api('/padaria');
        renderizar();
    } catch (erro) {
        listaProdutos.innerHTML = `<li class="vazio">Não foi possível carregar os produtos: ${escaparHtml(erro.message)}</li>`;
    }
}

document.getElementById('ano').textContent = new Date().getFullYear();
atualizarMenu();
carregarProdutos();
