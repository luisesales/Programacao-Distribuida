package com.bankai.mcpserver.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bankai.mcpserver.feign.AccountServiceFallback;
import com.bankai.mcpserver.model.Account;

@Component
public class BankAITools {

    @Autowired
    private AccountServiceFallback accountService;


    // @Tool(
    //     name = "verificarNomedoBanco",
    //     description = "Retorna o nome atual do Banco pela id recebida do banco"
    // )
    // public String verificarNomedoBanco() {
    //     System.out.println("Verificando nome atual do Banco " +bankId+ ": " + name);
    //     return "O nome atual do Banco " +bankId+ " : " + name;
    // }

    // @Tool(
    //     name = "mudarNomedoBanco",
    //     description = "Modifica o nome do banco dado que seja fornecido um nome para o Banco pela id e esse nome não seja igual ao atual"
    // )
    // public String mudarNomedoBanco(
    //     Long bankId,
    //     @ToolParam(description = "É uma string no formato de até 15 caracteres") String name 
    // ) {
    //     System.out.println("Mudnando nome"  +bankId+ " : " + name);
    //     return "Nome do banco alterado com sucesso!";
    // }

    // @Tool(
    //     name = "deletarContanoBanco",
    //     description = "Deleta uma conta no banco dado que seja fornecido um nome para a conta e uma conta com esse nome já exista"
    // )
    // public String deletarContanoBanco(
    //     Long accountId
    // ) {
    //     System.out.println("Deletando Conta " + accountId);
    //     return "Conta "+accountId+" deletada com sucesso!";
    // }

    @Tool(
        name = "criarContanoBanco",
        description = "Cria uma conta no banco dado que seja fornecido um nome para a conta e uma conta com esse nome já não exista"
    )
    public Account criarContanoBanco(
        @ToolParam(description = "@ToolParameter(name = \"id\", description = \"Identificador único da conta\", required = false)\n" + //
                        "    private Long id;\n" + //
                        "\n" + //
                        "    @ToolParameter(name = \"bankId\", description = \"Identificador do banco associado à conta\", required = true)\n" + //
                        "    private Long bankId;\n" + //
                        "\n" + //
                        "    @ToolParameter(name = \"name\", description = \"Nome do titular da conta\", required = true)\n" + //
                        "    private String name;\n" + //
                        "\n" + //
                        "    @ToolParameter(name = \"balance\", description = \"Saldo atual da conta\", required = false)\n" + //
                        "    private double balance;\n" + //
                        "\n" + //
                        "    @ToolParameter(name = \"isActive\", description = \"Indica se a conta está ativa\", required = false)\n" + //
                        "    private boolean isActive;") Account account
    ) {
        System.out.println("Entrei na Tool create Account do MCP Server");
        return accountService.checkCreateAvailability(account).getBody();
    }

    @Tool(
        name = "depositarNaContaNoBanco",
        description = "Deposita um valor x conta no banco dado que seja fornecido um valor numérico para depositar na conta e um id long considerando que uma conta com esse id já exista"
    )
    public Account depositarNaContaNoBanco(
        @ToolParam(description = "É um double que permite até dois caracteres flutuantes e um id em long") double value, Long accountId
    ) {
         System.out.println("Entrei na Tool deposit Account do MCP Server");
        return accountService.checkDepositAvailability(accountId,value).getBody();
    }

    @Tool(
        name = "sacarNaContaNoBanco",
        description = "Saca um valor x conta no banco dado que seja fornecido um valor numérico para depositar na conta e um id long considerando que uma conta com esse id já exista"
    )
    public Account sacarNaContaNoBanco(
        @ToolParam(description = "É um double que permite até dois caracteres flutuantes e um id em long") double value, Long accountId
    ) {
         System.out.println("Entrei na Tool draw Account do MCP Server");
        return accountService.checkDrawAvailability(accountId,value).getBody();
    }

    @Tool(
        name = "balancoNaContaNoBanco",
        description = "Verifica o saldo de uma conta no banco dado que seja fornecido um long id e uma conta com esse id já exista"
    )
    public Double balancoNaContaNoBanco(
        @ToolParam(description = "É uma id em long") Long accountId
    ) {
         System.out.println("Entrei na Tool balance Account do MCP Server");
        return accountService.checkBalanceAvailability(accountId).getBody();
    }
}
