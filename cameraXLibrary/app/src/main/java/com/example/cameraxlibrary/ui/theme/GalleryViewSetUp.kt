package com.example.cameraxlibrary.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun GalleryViewSetUp(list:List<Bitmap>,modifier: Modifier) {
    
    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalItemSpacing = 16.dp,
    contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ){
        items(list){
            imageBitmap ->
            Image(bitmap = imageBitmap.asImageBitmap(),contentDescription = null, modifier = Modifier.clip(
                RoundedCornerShape(12.dp)
            ) )
        }
    }
}