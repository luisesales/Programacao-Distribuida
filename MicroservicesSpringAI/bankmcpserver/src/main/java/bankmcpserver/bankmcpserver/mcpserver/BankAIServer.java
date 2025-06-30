package bankmcpserver.bankmcpserver.mcpserver;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import br.imd.ufrn.sigaamcpserver.service.DiscentesService;

@Component
public class BankAIServer {

    private final BankService bankService;

    public BankAIServer(BankService bankService) {
        this.bankService = bankService;
    }

    @Tool(
        name = "verificarNomedoBanco",
        description = "Retorna o nome atual do Banco salvo na variável name"
    )
    public String verificarNomedoBanco() {
        System.out.println("Verificando nome atual do BankAI: " + name);
        return "O nome atual do BankAI é: "+name;
    }

    @Tool(
        name = "mudarNomedoBanco",
        description = "Modifica o nome do banco dado que seja fornecido um nome para o banco e esse nome não seja igual ao atual"
    )
    public String mudarNomedoBanco(
        @ToolParam(description = "É uma string no formato de até 15 caracteres") String name
    ) {
        System.out.println("Mudnando nome" + name);
        return "Nome do banco alterado com sucesso!";
    }

    @Tool(
        name = "deletarContanoBanco",
        description = "Deleta uma conta no banco dado que seja fornecido um nome para a conta e uma conta com esse nome já exista"
    )
    public String deletarContanoBanco(
        @ToolParam(description = "É uma string no formato de até 15 caracteres") String name
    ) {
        System.out.println("Deletando Conta " + name);
        return "Conta "+name+" deletada com sucesso!";
    }

    @Tool(
        name = "criarContanoBanco",
        description = "Cria uma conta no banco dado que seja fornecido um nome para a conta e uma conta com esse nome já não exista"
    )
    public String criarContanoBanco(
        @ToolParam(description = "É uma string no formato de até 15 caracteres") String name
    ) {
        System.out.println("Criando Conta " + name);
        return "Conta "+name+" criada com sucesso!";
    }

    @Tool(
        name = "depositarnaContanoBanco",
        description = "Deposita uma valor x conta no banco dado que seja fornecido um valor numérico para depositar na conta e uma conta com esse nome já exista"
    )
    public String depositarnaContanoBanco(
        @ToolParam(description = "É um float que permite até dois caracteres flutuantes e um nome em string no formato de até 15 caracteres") float value, String name
    ) {
        System.out.println("Depositando na Conta "+name+ " o valor: " + value);
        return "valor: "+value+" depositado na conta "+name+ " com sucesso!";
    }

    public String balançodaContanoBanco(
        @ToolParam(description = "É uma string no formato de até 15 caracteres") String name
    ) {
        System.out.println(Verificando balanço na Conta " + name);
        return "balanço da conta:" +name+ " :" +value;
    }
}
