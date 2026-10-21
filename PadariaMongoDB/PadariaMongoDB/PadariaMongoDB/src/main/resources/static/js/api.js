// Funções comuns usadas por todas as páginas

// Quando o front é servido pelo próprio Spring, a API está na mesma origem.
// Se abrir o HTML pelo IntelliJ (porta 63342) ou Live Server (5500), usa o backend em localhost:8080.
const API_BASE = (location.protocol === 'file:' || ['63342', '5500'].includes(location.port))
    ? 'http://localhost:8080'
    : '';

const Sessao = {
    salvar(login, senha, dados) {
        const token = btoa(unescape(encodeURIComponent(`${login}:${senha}`)));
        sessionStorage.setItem('auth', token);
        sessionStorage.setItem('usuario', JSON.stringify(dados));
    },
    token() {
        return sessionStorage.getItem('auth');
    },
    usuario() {
        return JSON.parse(sessionStorage.getItem('usuario') || 'null');
    },
    logado() {
        return !!this.token();
    },
    sair() {
        sessionStorage.removeItem('auth');
        sessionStorage.removeItem('usuario');
    }
};

// fetch com JSON e autenticação; lança Error com a mensagem do backend
async function api(caminho, { method = 'GET', body } = {}) {
    const headers = {};
    if (body !== undefined) headers['Content-Type'] = 'application/json';
    if (Sessao.logado()) headers['Authorization'] = 'Basic ' + Sessao.token();

    let resposta;
    try {
        resposta = await fetch(API_BASE + caminho, {
            method,
            headers,
            body: body !== undefined ? JSON.stringify(body) : undefined
        });
    } catch (e) {
        throw new Error('Erro de conexão com o servidor.');
    }

    const texto = await resposta.text();
    const dados = texto ? JSON.parse(texto) : null;

    if (!resposta.ok) {
        if (resposta.status === 401 && Sessao.logado()) Sessao.sair();
        const erro = new Error((dados && dados.message) || `Erro ${resposta.status}`);
        erro.status = resposta.status;
        throw erro;
    }
    return dados;
}

// Carrinho guardado no navegador: [{ id, nome, preco, img, quantidade }]
const Carrinho = {
    itens() {
        return JSON.parse(localStorage.getItem('carrinho') || '[]');
    },
    salvar(itens) {
        localStorage.setItem('carrinho', JSON.stringify(itens));
    },
    adicionar(produto) {
        const itens = this.itens();
        const existente = itens.find(item => item.id === produto.id);
        if (existente) {
            existente.quantidade += 1;
        } else {
            itens.push({ id: produto.id, nome: produto.nome, preco: produto.preco, img: produto.imagem, quantidade: 1 });
        }
        this.salvar(itens);
    },
    total() {
        return this.itens().reduce((soma, item) => soma + item.preco * item.quantidade, 0);
    },
    quantidade() {
        return this.itens().reduce((soma, item) => soma + item.quantidade, 0);
    },
    limpar() {
        localStorage.removeItem('carrinho');
    }
};

function formatarPreco(valor) {
    return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function escaparHtml(texto) {
    const div = document.createElement('div');
    div.textContent = texto ?? '';
    return div.innerHTML;
}

// Manda para o login e volta para a página atual depois de entrar
function exigirLogin() {
    if (!Sessao.logado()) {
        const pagina = location.pathname.split('/').pop() || 'produtos.html';
        location.href = 'usu.html?voltar=' + encodeURIComponent(pagina);
        return false;
    }
    return true;
}

function sair() {
    Sessao.sair();
    location.href = 'usu.html';
}
