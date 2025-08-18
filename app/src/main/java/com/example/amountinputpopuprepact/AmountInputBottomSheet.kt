package com.example.amountinputpopuprepact

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FixedTransferAmountBottomSheet(
    scaffoldState: BottomSheetScaffoldState,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
    content: @Composable () -> Unit = {}
) {
    var input by remember { mutableStateOf("") }

    //  금액초기화
    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == BottomSheetValue.Collapsed) {
            input = ""
        }
    }

    fun updateInput(value: String) {
        if (input.length < 12) input += value
    }

    fun deleteLast() {
        input = input.dropLast(1)
    }

    fun addAmount(amount: Long) {
        val current = input.toLongOrNull() ?: 0L
        input = (current + amount).toString()
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = Color.White,
        sheetContent = {
            AmountSheetContent(
                input = input,
                onDismiss = onDismiss,
                onConfirm = onConfirm,
                onUpdateInput = ::updateInput,
                onDeleteLast = ::deleteLast,
                onAddAmount = ::addAmount,
                onResetInput = { input = "" }
            )
        }
    ) {
        content()
    }
}

//금액입력 화면 전체
@Composable
private fun AmountSheetContent(
    input: String,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
    onUpdateInput: (String) -> Unit,
    onDeleteLast: () -> Unit,
    onAddAmount: (Long) -> Unit,
    onResetInput: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        AmountHeader(input, onDismiss, onAddAmount, onResetInput)
        Spacer(modifier = Modifier.height(12.dp))
        AmountKeypad(onUpdateInput, onDeleteLast)
        ConfirmButton(input, onConfirm)
    }
}

//빠른 금액 버튼 + 현재 금액 표시
@Composable
private fun AmountHeader(
    input: String,
    onDismiss: () -> Unit,
    onAddAmount: (Long) -> Unit,
    onResetInput: () -> Unit
) {
    val formattedAmount = input.toLongOrNull()?.let {
        NumberFormat.getNumberInstance(Locale.KOREA).format(it)
    } ?: "0"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF949A8F))
            .padding(vertical = 12.dp)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                onResetInput()
            }) {
                Text(
                    "초기화",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(start = 20.dp)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "1만" to 10_000L,
                "5만" to 50_000L,
                "10만" to 100_000L,
                "100만" to 1_000_000L,
            ).forEach { (label, amount) ->
                TextButton(
                    onClick = { onAddAmount(amount) },
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Text(label, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                "$formattedAmount 원",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 25.dp)
            )
        }
    }
}

//숫자 키패드
@Composable
private fun AmountKeypad(
    onUpdateInput: (String) -> Unit,
    onDeleteLast: () -> Unit
) {
    val keyRows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("00", "0", "⌫")
    )

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp)
    ) {
        keyRows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { key ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clickable {
                                when (key) {
                                    "⌫" -> onDeleteLast()
                                    else -> onUpdateInput(key)
                                }
                            }
                    ) {
                        Text(
                            text = key,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

//확인 버튼
@Composable
private fun ConfirmButton(
    input: String,
    onConfirm: (Long) -> Unit
) {
    Button(
        onClick = {
            val amount = input.toLongOrNull() ?: 0L
            onConfirm(amount)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(top = 4.dp)
            .navigationBarsPadding(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF949A8F),
            contentColor = Color.White
        )
    ) {
        Text("확인", fontSize = 16.sp, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFixedTransferAmountBottomSheet() {
    val bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Expanded)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
    FixedTransferAmountBottomSheet(
        scaffoldState = scaffoldState,
        onDismiss = {},
        onConfirm = {},
        content = {
        }
    )
}



