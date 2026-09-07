package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.util.AppLanguage
import com.example.util.ProfileImageHelper
import java.io.File

val PRESET_AVATARS = listOf(
    "preset:🥑" to "Avocado",
    "preset:🥗" to "Salad",
    "preset:🏃" to "Runner",
    "preset:🧘" to "Yoga",
    "preset:🚴" to "Cyclist",
    "preset:🏋️" to "Fitness",
    "preset:🥦" to "Broccoli",
    "preset:🍎" to "Apple",
    "preset:💧" to "Hydration",
    "preset:⚡" to "Energy"
)

/**
 * Reusable Circular Avatar with support for:
 * 1. Custom Gallery/Camera Photos (loaded via Coil AsyncImage)
 * 2. Preset Avatars (preset:🥑)
 * 3. Fallback Initial Letter with dynamic theme gradient
 * 4. Optional Camera badge overlay for profile editing
 */
@Composable
fun UserProfileAvatar(
    imageUri: String?,
    userName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showEditBadge: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val initial = userName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Main Avatar Circle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Preset Emoji Avatar
                imageUri != null && imageUri.startsWith("preset:") -> {
                    val emoji = imageUri.removePrefix("preset:")
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = (size.value * 0.45f).sp
                        )
                    }
                }

                // Custom Image from Gallery or Camera
                !imageUri.isNullOrBlank() -> {
                    val context = LocalContext.current
                    val model = remember(imageUri) {
                        if (imageUri.startsWith("content://") || imageUri.startsWith("http")) {
                            imageUri
                        } else {
                            File(imageUri)
                        }
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(model)
                            .crossfade(true)
                            .build(),
                        contentDescription = "User Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                // Default Fallback: Styled Gradient with Initial
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            color = Color.White,
                            fontSize = (size.value * 0.42f).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Camera / Edit Overlay Badge
        if (showEditBadge) {
            Surface(
                modifier = Modifier
                    .size(size * 0.32f)
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Change photo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(size * 0.18f)
                    )
                }
            }
        }
    }
}

/**
 * Bottom Sheet / Dialog for choosing profile picture source:
 * - 📸 Camera (with permission check)
 * - 🖼️ Gallery (via Android Photo Picker)
 * - 🎨 Preset Avatars
 * - 🗑️ Remove Photo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePhotoPickerDialog(
    currentImageUri: String?,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onPhotoSelected: (String?) -> Unit
) {
    val context = LocalContext.current
    var tempCameraFile by remember { mutableStateOf<File?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Camera Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraFile?.let { file ->
                val savedPath = ProfileImageHelper.processCameraFileToInternal(context, file)
                if (savedPath != null) {
                    onPhotoSelected(savedPath)
                    Toast.makeText(
                        context,
                        if (lang == AppLanguage.TELUGU) "కెమెరా ఫోటో సెట్ చేయబడింది! 📸" else "Camera photo set! 📸",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        onDismiss()
    }

    // Camera Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val (file, uri) = ProfileImageHelper.createTempCameraUri(context)
                tempCameraFile = file
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    if (lang == AppLanguage.TELUGU) "కెమెరా తెరవడంలో లోపం ఏర్పడింది" else "Error launching camera",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                context,
                if (lang == AppLanguage.TELUGU) "ఫోటో తీయడానికి కెమెరా అనుమతి అవసరం" else "Camera permission is required to take photo",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Gallery Picker Launcher (Android Photo Picker - no permission required)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = ProfileImageHelper.copyUriToInternalFile(context, uri)
            if (savedPath != null) {
                onPhotoSelected(savedPath)
                Toast.makeText(
                    context,
                    if (lang == AppLanguage.TELUGU) "గ్యాలరీ ఫోటో సెట్ చేయబడింది! 🖼️" else "Gallery photo set! 🖼️",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "ప్రొఫైల్ ఫోటో ఎంపిక" else "Choose Profile Picture",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (lang == AppLanguage.TELUGU)
                        "కెమెరా లేదా గ్యాలరీ లేదా అవతార్ ఎంచుకోండి"
                    else
                        "Select from Camera, Gallery, or Wellness Avatars",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action Cards: Camera & Gallery
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Take Photo with Camera
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            )
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                try {
                                    val (file, uri) = ProfileImageHelper.createTempCameraUri(context)
                                    tempCameraFile = file
                                    tempCameraUri = uri
                                    cameraLauncher.launch(uri)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                        .testTag("button_pick_camera"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Camera",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "కెమెరా (Camera)" else "Camera",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "కొత్త ఫోటో తీయండి" else "Take new photo",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Choose from Gallery
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("button_pick_gallery"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Gallery",
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "గ్యాలరీ (Gallery)" else "Gallery",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lang == AppLanguage.TELUGU) "ఫోన్ నుండి ఎంచుకోండి" else "Pick from device",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Preset Avatars Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "లేదా వెల్‌నెస్ అవతార్ ఎంచుకోండి:" else "Or choose a Wellness Avatar:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(PRESET_AVATARS) { (presetKey, label) ->
                        val emoji = presetKey.removePrefix("preset:")
                        val isSelected = currentImageUri == presetKey

                        Surface(
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    onPhotoSelected(presetKey)
                                    onDismiss()
                                },
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = emoji, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }

            // Remove photo option if user already has one set
            if (!currentImageUri.isNullOrBlank()) {
                OutlinedButton(
                    onClick = {
                        ProfileImageHelper.removeProfileImage(context)
                        onPhotoSelected(null)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_remove_photo"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove photo",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "ప్రస్తుత ఫోటోను తీసివేయండి (Remove)" else "Remove Profile Photo",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
