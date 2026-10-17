package com.bidai.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bidai.app.ui.components.AuthWaveBackground
import com.bidai.app.ui.components.BidAiLogo
import com.bidai.app.ui.components.FigmaTextField
import com.bidai.app.ui.theme.BidAiBlue
import com.bidai.app.ui.theme.PureWhite
import com.bidai.app.ui.theme.TextBlack
import com.bidai.app.ui.theme.TextGray

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val form by viewModel.formState.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        // Decorative blue curves on top-right and bottom-left
        AuthWaveBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // 3iD.ai Logo at top left
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                BidAiLogo()
            }

            Spacer(modifier = Modifier.height(36.dp))

            if (form.isRegisterMode) {
                // SIGN UP SCREEN (Image 4 from Figma)
                Text(
                    text = "Join BID.ai",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "List fast. Earn smart",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "It only takes a click to start your resale journey",
                    fontSize = 13.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Name field
                FigmaTextField(
                    label = "Name",
                    value = form.fullName,
                    onValueChange = { viewModel.onFullNameChange(it) },
                    placeholder = "Syed",
                    isError = form.fullNameError != null,
                    errorMessage = form.fullNameError
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mobile Number field
                FigmaTextField(
                    label = "Mobile Number",
                    value = form.phone,
                    onValueChange = { viewModel.onPhoneChange(it) },
                    placeholder = "8657456666",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Gmail field
                FigmaTextField(
                    label = "Gmail",
                    value = form.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    placeholder = "abcd@gmail.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = form.emailError != null,
                    errorMessage = form.emailError
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Create Password field
                FigmaTextField(
                    label = "Create Password",
                    value = form.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    placeholder = "**** ****",
                    visualTransformation = if (form.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = form.passwordError != null,
                    errorMessage = form.passwordError,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (form.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = TextGray
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Confirm Password field
                FigmaTextField(
                    label = "Confirm Password",
                    value = form.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    placeholder = "**** ****",
                    visualTransformation = if (form.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = form.confirmPasswordError != null,
                    errorMessage = form.confirmPasswordError
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState is AuthUiState.Error) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            color = Color(0xFFC62828),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Sign Up Action Button
                val isLoading = uiState is AuthUiState.Loading

                Button(
                    onClick = { viewModel.submit(onAuthSuccess) },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BidAiBlue)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Sign Up",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Switch to Sign In link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "If you already have account ? ",
                        fontSize = 13.sp,
                        color = TextBlack
                    )
                    Text(
                        text = "Sign In",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BidAiBlue,
                        modifier = Modifier.clickable { viewModel.toggleRegisterMode() }
                    )
                }

            } else {
                // SIGN IN SCREEN (Image 3 from Figma)
                Text(
                    text = "Welcome Back  to Bid.ai",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sign in Your next deal is just a tap away.",
                    fontSize = 13.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Gmail field
                FigmaTextField(
                    label = "Gmail",
                    value = form.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    placeholder = "Enter Gmail",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = form.emailError != null,
                    errorMessage = form.emailError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                FigmaTextField(
                    label = "Password",
                    value = form.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    placeholder = "••••••••",
                    visualTransformation = if (form.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = form.passwordError != null,
                    errorMessage = form.passwordError,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (form.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = TextGray
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState is AuthUiState.Error) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            color = Color(0xFFC62828),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Sign In Action Button
                val isLoading = uiState is AuthUiState.Loading

                Button(
                    onClick = { viewModel.submit(onAuthSuccess) },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BidAiBlue)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Sign in",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Switch to Sign Up link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't you have an account? ",
                        fontSize = 13.sp,
                        color = TextBlack
                    )
                    Text(
                        text = "Sign up",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BidAiBlue,
                        modifier = Modifier.clickable { viewModel.toggleRegisterMode() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

