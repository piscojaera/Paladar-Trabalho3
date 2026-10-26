package com.example.trabalho2

import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.filled.Edit

@Composable
fun TelaHome(
    navController: NavController,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .padding(paddingValues)
            .padding(16.dp)
    ) {

        Text(
            text = "PALADAR",
            fontWeight = FontWeight.Bold,
            color = CorPrincipal
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Descubra restaurantes e organize seus favoritos.",
            color = CorTexto
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                navController.navigate(Rotas.RESTAURANTES)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver restaurantes")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                navController.navigate(Rotas.CATEGORIAS)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver categorias")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                navController.navigate(Rotas.SOBRE)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sobre o Paladar")
        }
    }
}

@Composable
fun TelaRestaurantes(
    navController: NavController,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {

    var nome by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf<Restaurante?>(null) }

    val context = LocalContext.current
    val dao = AppDatabase.getDatabase(context).restauranteDao()
    var lista by remember { mutableStateOf(emptyList<Restaurante>()) }

    suspend fun carregar() {
        try {
            lista = withContext(Dispatchers.IO) { dao.buscarTodos() }
        } catch (e: Exception) {
            Log.e("Paladar", "Erro ao buscar restaurantes", e)
        }
    }

    LaunchedEffect(Unit) { carregar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .padding(paddingValues)
            .padding(16.dp)
    ) {

        Text(
            text = "Restaurantes",
            fontWeight = FontWeight.Bold,
            color = CorTexto
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nota,
            onValueChange = { nota = it },
            label = { Text("Nota") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {

                val notaConvertida = nota.toDoubleOrNull()

                if (
                    nome.isNotBlank() &&
                    notaConvertida != null &&
                    preco.isNotBlank()
                ) {

                    val novo = Restaurante(
                        nome = nome,
                        nota = notaConvertida,
                        preco = preco,
                        categoriaId = 1,
                        descricao = descricao
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            dao.inserir(novo)
                            carregar()
                        } catch (e: Exception) {
                            Log.e("Paladar", "Erro ao inserir restaurante", e)
                        }
                    }

                    nome = ""
                    nota = ""
                    preco = ""
                    descricao = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar restaurante")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                items = lista,
                key = { it.id }
            ) { restaurante ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(
                                Rotas.detalheRestaurante(restaurante.id)
                            )
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = CorCard
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = restaurante.nome,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Nota: ${restaurante.nota}"
                            )

                            Text(
                                text = "Preço: ${restaurante.preco}"
                            )
                        }

                        IconButton(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        dao.deletar(restaurante)
                                        carregar()
                                    } catch (e: Exception) {
                                        Log.e("Paladar", "Erro ao remover restaurante", e)
                                    }
                                }

                            }
                        ) {

                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Remover"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TelaCategorias(
    navController: NavController,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .padding(paddingValues)
            .padding(16.dp)
    ) {

        Text(
            text = "Categorias",
            fontWeight = FontWeight.Bold,
            color = CorTexto
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome da categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {

                if (nome.isNotBlank()) {

                    val novoId = (categorias.maxOfOrNull { it.id } ?: 0) + 1

                    categorias.add(
                        Categoria(
                            id = novoId,
                            nome = nome,
                            descricao = descricao
                        )
                    )

                    nome = ""
                    descricao = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar categoria")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                items = categorias,
                key = { it.id }
            ) { categoria ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(
                                Rotas.detalheCategoria(categoria.id)
                            )
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = CorCard
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = categoria.nome,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = categoria.descricao
                            )
                        }

                        IconButton(
                            onClick = {
                                categorias.remove(categoria)
                            }
                        ) {

                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Remover"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalheRestaurante(
    id: Int,
    navController: NavController,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {

    val restaurante = restaurantes.find {
        it.id == id
    }

    if (restaurante == null) {
        return
    }

    val categoria = categorias.find {
        it.id == restaurante.categoriaId
    }

    val classificacao = when {
        restaurante.nota >= 4.5 -> "Excelente"
        restaurante.nota >= 4.0 -> "Muito bom"
        restaurante.nota >= 3.0 -> "Bom"
        else -> "Pode melhorar"
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {

            TopAppBar(
                title = {
                    Text(restaurante.nome)
                },
                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CorFundo)
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Text(
                text = restaurante.nome,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Nota: ${restaurante.nota}"
            )

            Text(
                text = "Preço: ${restaurante.preco}"
            )

            Text(
                text = "Categoria: ${categoria?.nome ?: "Sem categoria"}"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Classificação: $classificacao",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = restaurante.descricao
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {

                    val index = restaurantes.indexOfFirst {
                        it.id == restaurante.id
                    }

                    if (index != -1) {

                        restaurantes[index] =
                            restaurantes[index].copy(
                                favorito = !restaurantes[index].favorito
                            )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector =
                        if (restaurante.favorito)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    if (restaurante.favorito)
                        "Remover dos favoritos"
                    else
                        "Adicionar aos favoritos"
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalheCategoria(
    id: Int,
    navController: NavController,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {

    val categoria = categorias.find {
        it.id == id
    }

    if (categoria == null) {
        return
    }

    val restaurantesDaCategoria = restaurantes.filter {
        it.categoriaId == categoria.id
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {

            TopAppBar(
                title = {
                    Text(categoria.nome)
                },
                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CorFundo)
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Text(
                text = categoria.nome,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = categoria.descricao
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Restaurantes desta categoria",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (restaurantesDaCategoria.isEmpty()) {

                Text(
                    text = "Nenhum restaurante cadastrado nesta categoria."
                )

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(restaurantesDaCategoria) { restaurante ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    navController.navigate(
                                        Rotas.detalheRestaurante(
                                            restaurante.id
                                        )
                                    )
                                }
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = restaurante.nome,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Nota: ${restaurante.nota}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TelaFavoritos(
    navController: NavController,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {

    val favoritos = restaurantes.filter {
        it.favorito
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .padding(paddingValues)
            .padding(16.dp)
    ) {

        Text(
            text = "Favoritos",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (favoritos.isEmpty()) {

            Text(
                text = "Você ainda não possui restaurantes favoritos."
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(favoritos) { restaurante ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                navController.navigate(
                                    Rotas.detalheRestaurante(
                                        restaurante.id
                                    )
                                )
                            }
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = restaurante.nome,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Nota: ${restaurante.nota}"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TelaSobre(
    navController: NavController,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sobre o Paladar",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aplicativo desenvolvido para o Trabalho 2 da faculdade."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Voltar")
        }
    }
}