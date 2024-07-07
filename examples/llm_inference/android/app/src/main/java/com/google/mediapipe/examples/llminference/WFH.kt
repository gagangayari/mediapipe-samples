import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.width
import androidx.compose.ui.unit.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.mediapipe.examples.llminference.CHAT_SCREEN
import com.google.mediapipe.examples.llminference.LoadingIndicator
import com.google.mediapipe.examples.llminference.WFH_SCREEN


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
fun WorkFromHomeScreen(navController: NavController) {
    var fromDate by remember { mutableStateOf(TextFieldValue("05-Jul-2024")) }
    var toDate by remember { mutableStateOf(TextFieldValue("05-Jul-2024")) }
    var fromTime by remember { mutableStateOf(TextFieldValue("08:00")) }
    var toTime by remember { mutableStateOf(TextFieldValue("17:15")) }
    var assetUsed by remember { mutableStateOf(TextFieldValue("Infosys Laptop/Desktop")) }
    var connectivity by remember { mutableStateOf(TextFieldValue("Infosys network")) }
    var isAbleToWork by remember { mutableStateOf(true) }
    var isResidingInLocation by remember { mutableStateOf(true) }
    var isPolicyConfirmed by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apply Work From Home") },
//                backgroundColor = Color(0xFF6200EE),
//                contentColor = Color.White
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


                Button(
                    onClick = {
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
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextField(
                        value = fromDate,
                        onValueChange = { fromDate = it },
                        label = "From Date",
                        modifier = Modifier.weight(1f)
                    )
                    CustomTextField(
                        value = toDate,
                        onValueChange = { toDate = it },
                        label = "To Date",
                        modifier = Modifier.weight(1f)
                    )
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
                    value = TextFieldValue("Kamalkumar_R@infosys.com"),
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

//@Preview(showBackground = true)
//@Composable
//fun WorkFromHomeScreenPreview() {
//    WorkFromHomeScreen()
//}
