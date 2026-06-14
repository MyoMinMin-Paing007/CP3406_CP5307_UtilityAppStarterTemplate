package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.CP3406_CP5603UtilityAppStarterTemplateTheme
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel.CurrencyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CP3406_CP5603UtilityAppStarterTemplateTheme {
                UtilityApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    CP3406_CP5603UtilityAppStarterTemplateTheme {
        UtilityApp()
    }
}

@Composable
fun UtilityApp() {
    var selectedTab by remember { mutableStateOf("Utility") }
    val viewModel: CurrencyViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Utility") },
                    label = { Text("Utility") },
                    selected = selectedTab == "Utility",
                    onClick = { selectedTab = "Utility" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "Utility" -> UtilityScreen(viewModel)
                "Settings" -> SettingsScreen(viewModel)
            }
        }
    }
}

@Composable
fun UtilityScreen(viewModel: CurrencyViewModel) {
    val rates by viewModel.rates.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("SGD") }
    var result by remember { mutableStateOf<String?>(null) }

    val currencies = listOf("USD", "SGD", "EUR", "GBP", "JPY", "MMK", "AUD", "MYR")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Currency Converter", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("From", style = MaterialTheme.typography.labelLarge)
        CurrencyDropdown(
            selected = fromCurrency,
            options = currencies,
            onSelected = { fromCurrency = it }
        )

        Text("To", style = MaterialTheme.typography.labelLarge)
        CurrencyDropdown(
            selected = toCurrency,
            options = currencies,
            onSelected = { toCurrency = it }
        )

        Button(
            onClick = { viewModel.fetchRates(fromCurrency) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert")
        }

        when {
            isLoading -> CircularProgressIndicator()
            errorMessage != null -> Text(
                errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )
            rates != null && rates!!.isNotEmpty() -> {
                val rate = rates!![toCurrency]
                val amountDouble = amount.toDoubleOrNull()
                if (rate != null && amountDouble != null) {
                    val converted = "%.2f %s = %.2f %s".format(
                        amountDouble, fromCurrency, amountDouble * rate, toCurrency
                    )
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = converted,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyDropdown(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onSelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: CurrencyViewModel) {
    var baseCurrency by remember { mutableStateOf("USD") }
    val options = listOf("USD", "SGD", "EUR", "GBP", "MMK")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Default Base Currency", style = MaterialTheme.typography.bodyLarge)

        options.forEach { currency ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = baseCurrency == currency,
                    onClick = {
                        baseCurrency = currency
                        viewModel.fetchRates(currency)
                    }
                )
                Text(currency, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}