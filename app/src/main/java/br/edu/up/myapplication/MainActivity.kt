package br.edu.up.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.up.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

// Atividade principal, que inicializa a interface do usuário
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ativa o modo Edge-to-Edge
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    JornadaScreen(modifier = Modifier.padding(paddingValues))
                }
            }
        }
    }
}

// Função responsável por exibir a tela principal da jornada
@Composable
fun JornadaScreen(modifier: Modifier = Modifier) {
    // Variáveis de estado que controlam o andamento do jogo
    var jogoEmAndamento by remember { mutableStateOf(true) } // Controla se o jogo está ativo
    var metaCliques by remember { mutableStateOf(Random.nextInt(1, 51)) } // Define a meta de cliques aleatoriamente
    var contador by remember { mutableStateOf(0) } // Conta os cliques do usuário
    var dialogoReiniciar by remember { mutableStateOf(false) } // Controla a exibição do diálogo de reinício
    var dialogoConquista by remember { mutableStateOf(false) } // Controla a exibição do diálogo de conquista
    var mensagemDesistencia by remember { mutableStateOf(false) } // Controla a exibição da mensagem de desistência

    // Controla se a tela de conquista está sendo exibida
    var telaConquistaVisivel by remember { mutableStateOf(false) }

    // Array com as imagens da jornada
    val imagens = listOf(
        R.drawable.vila-medieval-noite, // Imagem inicial
        R.drawable.imagem-npc, // Imagem intermediária
        R.drawable.player-1, // Imagem final
        R.drawable.parabens, // Imagem de conquista
        R.drawable.player-1 // Imagem de desistência
    )

    // Seleciona a imagem atual com base no progresso
    val imagemAtual = when {
        contador >= metaCliques -> imagens[3] // Exibe a imagem de conquista
        contador >= metaCliques * 0.66 -> imagens[2] // Exibe a imagem intermediária final
        contador >= metaCliques * 0.33 -> imagens[1] // Exibe a imagem intermediária inicial
        else -> imagens[0] // Exibe a imagem inicial
    }

    // Define o texto que será exibido, com base no progresso do usuário
    val textoAtual = when {
        contador >= metaCliques -> "Parabéns! Você chegou ao fim da jornada. Sua determinação o levou ao triunfo. Desfrute da sensação de conquista e da paisagem que você alcançou."
        mensagemDesistencia -> "Você decidiu encerrar a jornada por aqui. Às vezes, a melhor escolha é saber a hora de parar. Mas lembre-se, você sempre pode tentar novamente."
        contador >= metaCliques * 0.66 -> "Agora você está em um lugar acolhedor e cheio de vida. O calor ao seu redor é reconfortante, e o fim da jornada está à vista. Continue, a vitória está próxima!"
        contador >= metaCliques * 0.33 -> "Você sente o ambiente ao seu redor aquecer. A jornada está se tornando mais confortável, mas ainda há um longo caminho a percorrer. Mantenha o foco e siga em frente!"
        else -> "Você está no início de sua jornada, em um lugar frio e sombrio. A escuridão ao seu redor desafia sua coragem. Continue avançando e veja se consegue encontrar a luz."
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Exibe o texto descritivo no topo da tela
        Text(
            text = textoAtual,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.7f), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Exibe a imagem correspondente ao progresso
        val imagemPainter: Painter = painterResource(id = imagemAtual)
        Image(
            painter = imagemPainter,
            contentDescription = "Imagem da Jornada",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Botões de interação do usuário
        if (jogoEmAndamento) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = {
                        if (contador < metaCliques) {
                            contador++
                        }
                        if (contador >= metaCliques) {
                            jogoEmAndamento = false
                            telaConquistaVisivel = true
                        }
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Clique aqui: $contador")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        jogoEmAndamento = false
                        dialogoReiniciar = true
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Desistir")
                }
            }
        }
    }

    // Diálogo para reiniciar o jogo
    if (dialogoReiniciar) {
        AlertDialog(
            onDismissRequest = { dialogoReiniciar = false },
            title = { Text("Novo jogo?") },
            text = { Text("Deseja iniciar um novo jogo?") },
            confirmButton = {
                Button(
                    onClick = {
                        jogoEmAndamento = true
                        metaCliques = Random.nextInt(1, 51)
                        contador = 0
                        dialogoReiniciar = false
                    }
                ) {
                    Text("Sim")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mensagemDesistencia = true
                        dialogoReiniciar = false
                    }
                ) {
                    Text("Não")
                }
            }
        )
    }

    // Exibe a tela de conquista por 5 segundos antes de mostrar o diálogo de reinício
    if (telaConquistaVisivel) {
        LaunchedEffect(Unit) {
            delay(5000) // Espera 5 segundos
            dialogoConquista = true
            telaConquistaVisivel = false
        }
    }

    // Diálogo exibido após a conquista
    if (dialogoConquista) {
        AlertDialog(
            onDismissRequest = { dialogoConquista = false },
            title = { Text("Parabéns!") },
            text = { Text("Você alcançou a meta!") },
            confirmButton = {
                Button(
                    onClick = {
                        jogoEmAndamento = true
                        metaCliques = Random.nextInt(1, 51)
                        contador = 0
                        dialogoConquista = false
                    }
                ) {
                    Text("Reiniciar")
                }
            }
        )
    }
}

// Função de visualização para o modo de design
@Preview(showBackground = true)
@Composable
fun JornadaScreenPreview() {
    MyApplicationTheme {
        JornadaScreen()
    }
}
