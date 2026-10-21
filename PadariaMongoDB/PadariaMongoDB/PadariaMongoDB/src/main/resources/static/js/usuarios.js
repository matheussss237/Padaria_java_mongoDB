// Cadastro (index.html), login (usu.html) e "Minha conta" (atualizar.html)

function somenteDigitos(valor) {
    return valor.replace(/\D/g, '');
}

// CADASTRAR USUÁRIO
const formCadastro = document.getElementById('formCadastro');
if (formCadastro) {
    formCadastro.addEventListener('submit', async (e) => {
        e.preventDefault();
        const cpf = somenteDigitos(document.getElementById('cpf').value);
        const senha = document.getElementById('senha').value;

        const usuario = {
            nome: document.getElementById('nome').value.trim(),
            cpf,
            email: document.getElementById('email').value.trim() || null,
            dataNascimento: document.getElementById('nascimento').value || null,
            telefone: somenteDigitos(document.getElementById('telefone').value) || null,
            senha
        };

        try {
            const criado = await api('/auth/register', { method: 'POST', body: usuario });
            Sessao.salvar(cpf, senha, { login: cpf, role: 'user', nome: criado.nome });
            alert('Cadastro realizado com sucesso!');
            location.href = 'produtos.html';
        } catch (erro) {
            alert('Erro ao cadastrar: ' + erro.message);
        }
    });
}

// LOGIN
const formLogin = document.getElementById('formLogin');
if (formLogin) {
    formLogin.addEventListener('submit', async (e) => {
        e.preventDefault();
        const campoLogin = document.getElementById('usuario').value.trim();
        // CPF pode ser digitado com pontos e traço; o admin entra com o nome de login
        const login = /^[\d.\-\s]+$/.test(campoLogin) ? somenteDigitos(campoLogin) : campoLogin;
        const senha = document.getElementById('senha').value;

        try {
            const dados = await api('/auth/login', { method: 'POST', body: { login, senha } });
            Sessao.salvar(login, senha, dados);
            const voltar = new URLSearchParams(location.search).get('voltar');
            location.href = voltar && /^[a-z]+\.html$/.test(voltar) ? voltar : 'produtos.html';
        } catch (erro) {
            alert(erro.status === 401 ? 'Login ou senha inválidos.' : erro.message);
        }
    });
}

// MINHA CONTA: buscar, atualizar e excluir
const formAtualizar = document.getElementById('formAtualizar');
if (formAtualizar && exigirLogin()) {
    const login = Sessao.usuario().login;

    async function carregarConta() {
        try {
            const usuario = await api(`/usuarios/${encodeURIComponent(login)}`);
            document.getElementById('nome').value = usuario.nome || '';
            document.getElementById('cpf').value = usuario.cpf || '';
            document.getElementById('email').value = usuario.email || '';
            document.getElementById('data_nascimento').value = usuario.dataNascimento || '';
            document.getElementById('telefone').value = usuario.telefone || '';
        } catch (erro) {
            alert(erro.status === 404
                ? 'Esse login não possui cadastro de cliente.'
                : 'Erro ao buscar seus dados: ' + erro.message);
            if (erro.status === 401) sair();
        }
    }

    async function carregarCompras() {
        const lista = document.getElementById('minhasCompras');
        try {
            const compras = await api('/compras/minhas');
            if (compras.length === 0) {
                lista.innerHTML = '<li>Você ainda não fez nenhuma compra.</li>';
                return;
            }
            lista.innerHTML = compras.map(compra => `
                <li>
                    <strong>${new Date(compra.dataCompra).toLocaleString('pt-BR')}</strong> —
                    ${compra.produtos.length} item(ns) — ${formatarPreco(compra.valorTotal)}
                </li>`).join('');
        } catch (erro) {
            lista.innerHTML = '<li>Não foi possível carregar suas compras.</li>';
        }
    }

    formAtualizar.addEventListener('submit', async (e) => {
        e.preventDefault();
        const dados = {
            nome: document.getElementById('nome').value.trim(),
            email: document.getElementById('email').value.trim(),
            dataNascimento: document.getElementById('data_nascimento').value || null,
            telefone: somenteDigitos(document.getElementById('telefone').value)
        };
        const novaSenha = document.getElementById('senha').value;

        try {
            const atualizado = await api(`/usuarios/${encodeURIComponent(login)}`, { method: 'PUT', body: dados });
            if (novaSenha) {
                await api('/auth/senha', { method: 'PUT', body: { senha: novaSenha } });
                Sessao.salvar(login, novaSenha, { ...Sessao.usuario(), nome: atualizado.nome });
                document.getElementById('senha').value = '';
            }
            alert('Cadastro atualizado com sucesso!');
        } catch (erro) {
            alert('Erro ao atualizar: ' + erro.message);
        }
    });

    document.getElementById('btnExcluir').addEventListener('click', async () => {
        if (!confirm('Tem certeza que deseja excluir sua conta?')) return;
        try {
            await api(`/usuarios/${encodeURIComponent(login)}`, { method: 'DELETE' });
            alert('Conta excluída.');
            Carrinho.limpar();
            sair();
        } catch (erro) {
            alert('Erro ao excluir: ' + erro.message);
        }
    });

    carregarConta();
    carregarCompras();
}
