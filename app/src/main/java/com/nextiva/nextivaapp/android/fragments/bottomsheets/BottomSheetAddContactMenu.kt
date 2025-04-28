package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetAddContactMenuBinding
import com.nextiva.nextivaapp.android.viewmodels.ConnectContactDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetAddContactMenu() : BaseBottomSheetDialogFragment() {

    private lateinit var composeView: ComposeView

    private lateinit var viewModel: ConnectContactDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ConnectContactDetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_add_contact_menu, container, false)
        view?.let { bindViews(view) }

        return view
    }

    private fun bindViews(view: View) {
        composeView = BottomSheetAddContactMenuBinding.bind(view).composeView
        composeView.setContent {
            Buttons(
                arguments?.getString(BUTTON1),
                arguments?.getString(BUTTON2),
                arguments?.getString(BUTTON3),
                { index ->
                    viewModel.onAddContactPressed(index)
                    dismiss()
                }
            )
        }
    }

    companion object {

        private const val BUTTON1 = "button1"
        private const val BUTTON2 = "button2"
        private const val BUTTON3 = "button3"

        fun newInstance(
            button1: String?,
            button2: String?,
            button3: String?
        ) = BottomSheetAddContactMenu().apply {
            arguments = Bundle().apply {
                putString(BUTTON1, button1)
                putString(BUTTON2, button2)
                putString(BUTTON3, button3)
            }
        }
    }
}

@Composable
private fun Buttons(
    button1: String?,
    button2: String?,
    button3: String?,
    onClick: ((Int) -> Unit)
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Image(

            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(R.drawable.ic_bottom_sheet_pull_down),
            contentDescription = null
        )

        button1?.let { button1 ->
            TextButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = 8.dp)
                    .padding(start = 16.dp),
                shape = RoundedCornerShape(4.dp),
                onClick = { onClick(0) }
            ) {
                Text(
                    text = button1,
                    style = TextStyle(
                        color = colorResource(id = R.color.connectWhite),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.lato_regular)),
                    ),
                    color = colorResource(R.color.connectSecondaryDarkBlue),
                )
            }
        }

        button2?.let { button2 ->
            TextButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = 8.dp)
                    .padding(start = 16.dp),
                shape = RoundedCornerShape(4.dp),
                onClick = { onClick(1) }
            ) {
                Text(
                    text = button2,
                    style = TextStyle(
                        color = colorResource(id = R.color.connectSecondaryDarkBlue),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.lato_regular)),
                    ),
                    color = colorResource(R.color.connectSecondaryDarkBlue),
                )
            }
        }


        button3?.let { button3 ->

            TextButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
                    .padding(start = 16.dp),
                shape = RoundedCornerShape(4.dp),
                onClick = { onClick(2) }
            ) {
                Text(
                    text = button3,
                    style = TextStyle(
                        color = colorResource(id = R.color.connectSecondaryDarkBlue),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.lato_regular)),
                    ),
                    color = colorResource(R.color.connectSecondaryDarkBlue),
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    Buttons(
        "Add to contact",
        "Add to existing contact",
        "Add to Local contacts",
        {}
    )
}