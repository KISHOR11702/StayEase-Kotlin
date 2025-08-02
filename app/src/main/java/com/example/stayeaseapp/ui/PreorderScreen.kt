package com.example.stayeaseapp.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

// --- Data classes ---
data class MenuItem(
    val id: String = "",
    val createdAt: Timestamp? = null,
    val day: String = "",
    val deadline: String = "",
    val food: String = "",
    val imageUrl: String = ""
)

data class PreorderItem(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val menuItemId: String = "",
    val food: String = "",
    val day: String = "",
    val quantity: Int = 1,
    val orderTime: String = "",
    val status: String = "pending"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreorderScreen(navController: NavController, studentEmail: String, studentName: String) {
    val context = LocalContext.current
    var menuItems by remember { mutableStateOf(listOf<MenuItem>()) }
    var preorderItems by remember { mutableStateOf(listOf<PreorderItem>()) }
    var isLoading by remember { mutableStateOf(true) }
    val db = FirebaseFirestore.getInstance()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedMenuItem by remember { mutableStateOf<MenuItem?>(null) }
    var selectedQuantity by remember { mutableStateOf(1) }

    // Refresh Orders
    val refreshOrders = {
        isLoading = true
        db.collection("preorderslist")
            .whereEqualTo("studentId", studentEmail)
            .orderBy("orderTime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                preorderItems = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PreorderItem::class.java)?.copy(id = doc.id)
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("PreorderScreen", "Error fetching preorders: ${e.message}")
                isLoading = false
            }
    }

    LaunchedEffect(true) {
        try {
            isLoading = true
            // Fetch menu items
            val menuSnapshot = db.collection("preorders")
                .get()
                .await()
            
            menuItems = menuSnapshot.documents.mapNotNull { doc ->
                doc.toObject<MenuItem>()?.copy(id = doc.id)
            }

            // Fetch preorders
            val preorderSnapshot = db.collection("preorderslist")
                .whereEqualTo("studentId", studentEmail)
                .orderBy("orderTime", Query.Direction.DESCENDING)
                .get()
                .await()

            preorderItems = preorderSnapshot.documents.mapNotNull { doc ->
                doc.toObject(PreorderItem::class.java)?.copy(id = doc.id)
            }
            
            isLoading = false
        } catch (e: Exception) {
            Log.e("PreorderScreen", "Error fetching data: ${e.message}")
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pre-Orders", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = "Food",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Food Pre-Orders",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Place your orders in advance",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            // View Preorders Button
            ElevatedButton(
                onClick = { navController.navigate("viewPreorders/$studentEmail/$studentName") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "View Orders",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "View My Pre-Orders",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = "Available Menu Items",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(menuItems) { menuItem ->
                    MenuItemCard(
                        menuItem = menuItem,
                        studentEmail = studentEmail,
                        studentName = studentName,
                        db = db,
                        navController = navController,
                        onOrderPlaced = { refreshOrders() },
                        onConfirmOrder = { item, quantity ->
                            selectedMenuItem = item
                            selectedQuantity = quantity
                            showConfirmationDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = {
                Text(
                    "Confirm Order",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Are you sure you want to place this order?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    selectedMenuItem?.let { item ->
                        Text(
                            "Item: ${item.food}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Day: ${item.day}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Quantity: $selectedQuantity",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedMenuItem?.let { item ->
                            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .format(Date())

                            val preorder = hashMapOf(
                                "studentId" to studentEmail,
                                "studentName" to studentName,
                                "menuItemId" to item.id,
                                "food" to item.food,
                                "day" to item.day,
                                "quantity" to selectedQuantity,
                                "orderTime" to currentTime,
                                "status" to "pending"
                            )

                            db.collection("preorderslist")
                                .add(preorder)
                                .addOnSuccessListener { documentRef ->
                                    val qrData = """
                                        Order ID: ${documentRef.id}
                                        Student: $studentName
                                        Food: ${item.food}
                                        Day: ${item.day}
                                        Quantity: $selectedQuantity
                                        Time: $currentTime
                                    """.trimIndent()

                                    navController.navigate("qrSuccess/${qrData}")
                                    showConfirmationDialog = false
                                    refreshOrders()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("PreorderScreen", "Error placing order: ${e.message}")
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Confirm Order")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    studentEmail: String,
    studentName: String,
    db: FirebaseFirestore,
    navController: NavController,
    onOrderPlaced: () -> Unit,
    onConfirmOrder: (MenuItem, Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(menuItem.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = menuItem.food,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 ${menuItem.day}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "⏰ ${menuItem.deadline}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quantity:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Text(
                        text = "$quantity",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { if (quantity < 5) quantity++ },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }

            Button(
                onClick = { onConfirmOrder(menuItem, quantity) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Order",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Place Order")
            }
        }
    }
}
