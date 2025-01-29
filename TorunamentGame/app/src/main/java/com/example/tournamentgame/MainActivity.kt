package com.example.tournamentgame

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import com.example.tournamentgame.ui.theme.TournamentGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TournamentGameTheme {
                TournamentGame()
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TournamentGame() {
    // Alapállapot
    var userName by remember { mutableStateOf("") }
    var gameStarted by remember { mutableStateOf(false) }
    var showWelcomeScreen by remember { mutableStateOf(true) }

    // A képek listája
    val imagePool = remember { mutableStateListOf(
        R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
        R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8,
        R.drawable.image9, R.drawable.image10, R.drawable.image11, R.drawable.image12,
        R.drawable.image13, R.drawable.image14, R.drawable.image15, R.drawable.image16
    ) }

    // Összekeverés és párok kialakítása
    var currentRound by remember { mutableStateOf(imagePool.shuffled().chunked(2)) }
    var currentPairIndex by remember { mutableIntStateOf(0) }
    var winnersList by remember { mutableStateOf(mutableListOf<Int>()) }
    var winnerImage by remember { mutableStateOf<Int?>(null) }
    var finalWinner by remember { mutableStateOf<Int?>(null) }
    var roundNumber by remember { mutableIntStateOf(1) }
    var showWinner by remember { mutableStateOf(false) }

    // Győztes oldal választása
    var winnerSide by remember { mutableStateOf("") }

    // Animáció offset meghatározása a győztes alapján
    val xOffset = remember { Animatable(0f) }
    val yOffset = remember { Animatable(0f) }

    // Győztes esetén trigger
    LaunchedEffect(winnerImage) {
        if (winnerImage != null) {
            // Győztes oldal meghatározása
            winnerSide = if (winnerImage == currentRound[currentPairIndex][0]) "left" else "right"

            // Offset meghatározása
            xOffset.snapTo(if (winnerSide == "left") -200f else 200f)
            yOffset.snapTo(0f)

            // Középre animálás
            xOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
            yOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        }
    }

    // Üdvözlő Screen
    if (showWelcomeScreen) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to the Tournament Game!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 48.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(lineHeight = 40.sp)
            )
            Button(
                onClick = { showWelcomeScreen = false },
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text("Play")
            }
        }
    } else if (!gameStarted) {
        // Felhasználónév Screen
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter your name:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 48.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(lineHeight = 32.sp)
            )
            BasicTextField(
                value = userName,
                onValueChange = { userName = it },
                textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            )
            Button(
                onClick = { gameStarted = true },
                enabled = userName.isNotEmpty(),
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text("Start Game")
            }
        }
    } else {
        // Tournament Game
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (finalWinner != null) {
                // Végső győztes
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$userName, this is your final winner!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 32.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(lineHeight = 40.sp)
                    )
                    winnerImage?.let { winner ->
                        Image(
                            painter = painterResource(id = winner),
                            contentDescription = null,
                            modifier = Modifier
                                .size(250.dp)
                                .padding(bottom = 32.dp)
                        )
                    }
                    Button(onClick = {
                        // Reset game
                        currentRound = imagePool.shuffled().chunked(2)
                        currentPairIndex = 0
                        winnersList.clear()
                        winnerImage = null
                        finalWinner = null
                        roundNumber = 1
                        showWinner = false
                        gameStarted = false // Start
                        showWelcomeScreen = true // Welcome
                    }) {
                        Text("Restart")
                    }
                }
            } else {
                // Kör számláló
                Text(
                    text = "Round $roundNumber (${currentPairIndex + 1}/${currentRound.size})",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (showWinner) {
                    // Középre animálás
                    winnerImage?.let { winner ->
                        AnimatedVisibility(
                            visible = showWinner,
                            enter = fadeIn() + slideInHorizontally(
                                initialOffsetX = { if (winnerSide == "left") -it else it }
                            ) + slideInVertically(
                                initialOffsetY = { -it / 2 }
                            ),
                            exit = fadeOut() + slideOutHorizontally(
                                targetOffsetX = { it / 2 }
                            ) + slideOutVertically(
                                targetOffsetY = { it / 2 }
                            )
                        ) {
                            Image(
                                painter = painterResource(id = winner),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(150.dp)
                                    .graphicsLayer(
                                        translationX = xOffset.value,
                                        translationY = yOffset.value
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        // Következő pár
                        winnersList.add(winnerImage!!)
                        if (currentPairIndex + 1 < currentRound.size) {
                            currentPairIndex++
                            showWinner = false
                        } else {
                            if (winnersList.size == 1) {
                                finalWinner = winnersList.first()
                            } else {
                                currentRound = winnersList.shuffled().chunked(2)
                                currentPairIndex = 0
                                winnersList = mutableListOf()
                                roundNumber++
                            }
                            showWinner = false
                        }
                    }) {
                        Text("Next")
                    }
                } else {
                    val pair = currentRound.getOrNull(currentPairIndex) ?: emptyList()
                    if (pair.size == 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            pair.forEach { image ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = image),
                                        contentDescription = null,
                                        modifier = Modifier.size(150.dp)
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(onClick = {
                                        // Választás
                                        winnerImage = image
                                        showWinner = true
                                    }) {
                                        Text("Choose")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}