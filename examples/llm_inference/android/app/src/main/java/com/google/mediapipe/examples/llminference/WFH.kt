
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.mediapipe.examples.llminference.CHAT_SCREEN
import com.google.mediapipe.examples.llminference.ChatViewModel
import com.google.mediapipe.examples.llminference.WFH_SCREEN
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


//class WorkFromHomeActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            WorkFromHomeScreen(navController = )
//        }
//    }
//}

interface WFHInterface {
    var fromDate : String
    var toDate : String
    var fromTime : String
    var toTime : String
    var approver : String
    var asset : String
    var network : String

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkFromHomeScreen(
    navController: NavController,
    chatViewModel: ChatViewModel ) {

    val wfhDetails by chatViewModel.showWFHDetails.collectAsStateWithLifecycle()

    var fromDate by remember { mutableStateOf(TextFieldValue("05-Jul-2024")) }
    var toDate by remember { mutableStateOf(TextFieldValue("05-Jul-2024")) }
    var fromTime by remember { mutableStateOf(TextFieldValue("08:00")) }
    var toTime by remember { mutableStateOf(TextFieldValue("17:15")) }
    var approver by remember { mutableStateOf(TextFieldValue("Kamalkumar_R@infosys.com")) }
    var assetUsed by remember { mutableStateOf(TextFieldValue("Infosys Laptop/Desktop")) }
    var connectivity by remember { mutableStateOf(TextFieldValue("Infosys network")) }
    var isAbleToWork by remember { mutableStateOf(true) }
    var isResidingInLocation by remember { mutableStateOf(true) }
    var isPolicyConfirmed by remember { mutableStateOf(false) }


    val wfhJson = JSONObject(wfhDetails)
//    Log.i("*** Chatview data", wfhJson.toString())


        fromDate = TextFieldValue(wfhJson.getString("fromDate"))
        toDate = TextFieldValue(wfhJson.getString("toDate"))
        fromTime = TextFieldValue(wfhJson.getString("fromTime"))
        toTime = TextFieldValue(wfhJson.getString("toTime"))
        assetUsed = TextFieldValue(wfhJson.getString("Asset"))
        approver = TextFieldValue(wfhJson.getString("Approver")+"@infosys.com")
        connectivity = TextFieldValue(wfhJson.getString("Network"))





//    val wfhInstance: WFHData = gson.fromJson(jsonString, WFHData::class.java)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apply Work From Home") },
//                backgroundColor = Color(0xFF6200EE),
//                contentColor = Color.White
                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White,
                ),
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {



                Text(
                    text = "Apply Work From Home",

                    fontSize = 20.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray)
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    CustomTextField(
//                        value = fromDate,
//                        onValueChange = { fromDate = it },
//                        label = "From Date",
//                        modifier = Modifier.weight(1f)
//                    )
                    
                    DateFieldExample(date = fromDate, text = "From Date")
                    DateFieldExample(date = toDate, text = "To Date")

//                    CustomTextField(
//                        value = toDate,
//                        onValueChange = { toDate = it },
//                        label = "To Date",
//                        modifier = Modifier.weight(1f)
//                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomTextField(
                        value = fromTime,
                        onValueChange = { fromTime = it },
                        label = "From Time",
                        modifier = Modifier.weight(1f)
                    )
                    CustomTextField(
                        value = toTime,
                        onValueChange = { toTime = it },
                        label = "To Time",
                        modifier = Modifier.weight(1f)
                    )
                }

                CustomTextField(
                    value = assetUsed,
                    onValueChange = { assetUsed = it },
                    label = "Asset Used",
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "I am able to work",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = isAbleToWork,
                        onClick = { isAbleToWork = true },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6200EE))
                    )
                    Text(text = "Yes", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = !isAbleToWork,
                        onClick = { isAbleToWork = false },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6200EE))
                    )
                    Text(text = "No", fontSize = 14.sp)
                }

                CustomTextField(
                    value = connectivity,
                    onValueChange = { connectivity = it },
                    label = "Connectivity",
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "For the purpose of business continuity and planning, it's important for the organisation to know your current location.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "As per our record (in Harmony) your PRESENT address is:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Text(
                    text = "Gurihati,Khoirabari, PO- Khoirabari, Guwahati(GAU), ASSAM, 784522, Phone Number-8486528128",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = "Are you residing today in this location?",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = isResidingInLocation,
                        onClick = { isResidingInLocation = true },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6200EE))
                    )
                    Text(text = "Yes", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = !isResidingInLocation,
                        onClick = { isResidingInLocation = false },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6200EE))
                    )
                    Text(text = "No", fontSize = 14.sp)
                }

                CustomTextField(
                    value = approver,
                    onValueChange = { /* Handle change */ },
                    label = "To be approved by",
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPolicyConfirmed,
                        onCheckedChange = { isPolicyConfirmed = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6200EE))
                    )
                    Text(
                        text = "As per India Working Hours Policy applicable to me, I am aware that I can work only in India. Thereby, I confirm that I am currently working only in India, and not from any country outside India.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = {
                        //Turn off showWFH
                        chatViewModel.setShowWFH(false)
                        chatViewModel.uiState.value.appendMessage("000","Work from home submitted", true)
                        navController.navigate(CHAT_SCREEN) {
                            popUpTo(WFH_SCREEN) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Submit")
                }


            }
        }
    )
}

@Composable
fun CustomTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .border(1.dp, Color.Gray)
                .padding(8.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}



@Composable
fun DateFieldExample(date : TextFieldValue, text : String) {
    var dateText by remember { mutableStateOf((date)) }
    val context = LocalContext.current

    // Function to show DatePickerDialog
    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormatter = SimpleDateFormat("dd/mmm/yyyy", Locale.getDefault())
                dateText = TextFieldValue(dateFormatter.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

        TextField(
            value = dateText,
            onValueChange = { dateText = it },
            readOnly = true,
            label = { Text(text) },
            modifier = Modifier.width(180.dp)
                .clickable { showDatePickerDialog() } ,

        )
}

//@Preview(showBackground = true)
//@Composable
//fun WorkFromHomeScreenPreview() {
//    WorkFromHomeScreen(NavController(LocalContext.current), ChatViewModel(InferenceModel.getInstance(
//        LocalContext.current)))
//}
