package com.bignerdranch.android.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: View
    private lateinit var sunView: View
    private lateinit var skyView: View
    private lateinit var sunReflection: View
    private lateinit var seaView: View
    private var sunState = true

    private val blueSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.blue_sky)
    }
    private val sunsetSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.sunset_sky)
    }
    private val nightSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.night_sky)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()  //скрыть actionBar

        sceneView = findViewById(R.id.scene)
        sunView = findViewById(R.id.sun)
        skyView = findViewById(R.id.sky)
        sunReflection = findViewById(R.id.imgSunReflection)
        seaView = findViewById(R.id.imgSeaView)

        sceneView.setOnClickListener {
            animation()
        }
    }

    private fun animation() {
        sunState = if(sunState) {
            startAnimation()
            false
        } else {
            reverseAnimation()
            true
        }
    }

    private fun reverseAnimation() {

        val sunYStart = sunView.top.toFloat()
        val sunYEnd = skyView.top.toFloat()

        val heightAnimator2 = ObjectAnimator
            .ofFloat(sunView, "y", sunYEnd, sunYStart)
            .setDuration(3000)
        heightAnimator2.interpolator = AccelerateInterpolator()

        val sunsetSkyAnimator2 = ObjectAnimator
            .ofInt(skyView, "backgroundColor", blueSkyColor, sunsetSkyColor)
            .setDuration(3000)
        sunsetSkyAnimator2.setEvaluator(ArgbEvaluator())

        val blueSkyColor = ObjectAnimator
            .ofInt(skyView, "backgroundColor", sunsetSkyColor, blueSkyColor)
            .setDuration(1500)
        blueSkyColor.setEvaluator(ArgbEvaluator())

        val refStart = sunReflection.top.toFloat()
        val refEnd = seaView.height.toFloat()

        val sunReflectAnimator = ObjectAnimator
            .ofFloat(sunReflection, "y", refEnd, refStart)
            .setDuration(3000)

        val animatorSet2 = AnimatorSet()
        animatorSet2.play(heightAnimator2) //воспроизвести heightAnimator с sunsetSky, также воспроизвести heightAnimator перед blueSky
            .with(sunsetSkyAnimator2)
            .with(sunReflectAnimator)
            .before(blueSkyColor)

        animatorSet2.start()
    }


    private fun startAnimation() {

        //определить начальное и конечное состояния анимации
        val sunYStart = sunView.top.toFloat()
        val sunYEnd = skyView.height.toFloat()

        val heightAnimator = ObjectAnimator
            .ofFloat(sunView, "y", sunYStart, sunYEnd)
            .setDuration(3000)
        /* обеспечит небольшое ускорение солнца в начале анимации с
        * использованием объекта AccelerateInterpolator*/
        heightAnimator.interpolator = AccelerateInterpolator()

        /*дополнительную анимацию цвета неба от blueSkyColor до sunsetSkyColor*/
        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", blueSkyColor, sunsetSkyColor)
            .setDuration(3000)
        /*научим нашу анимацию правильно изменять цвет*/
        sunsetSkyAnimator.setEvaluator(ArgbEvaluator())

        /*анимацию ночного неба */
        val nightSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", sunsetSkyColor, nightSkyColor)
            .setDuration(1500)
        nightSkyAnimator.setEvaluator(ArgbEvaluator())

        /*синхроный запуск анимаций*//*
        heightAnimator.start()
        sunsetSkyAnimator.start()*/

        val sunYHeatStart = 0f
        val sunYHeatEnd = 360f

        val heatAnimator = ObjectAnimator
            .ofFloat(sunView, "rotation", sunYHeatStart, sunYHeatEnd)
            .setDuration(9000)
        heatAnimator.repeatCount = ObjectAnimator.INFINITE

        val refStart = sunReflection.top.toFloat()
        val refEnd = seaView.height.toFloat()

        val sunReflectAnimator = ObjectAnimator
            .ofFloat(sunReflection, "y", refStart, refEnd)
            .setDuration(3000)

        /*запуск AnimatorSet для последовательного выполнения анимаций*/
        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunsetSkyAnimator)
            .with(sunReflectAnimator)
            .with(heatAnimator)
            .before(nightSkyAnimator)
        animatorSet.start()
        /* При вызове функции play(Animator) вы получаете объект AnimatorSet.Builder,
        * который позволяет построить цепочку инструкций
        *  «Воспроизвести heightAnimator с sunsetSkyAnimator; также воспроизвести
        * heightAnimator до nightSkyAnimator». Возможно, в сложных разновидностях
        * AnimatorSet потребуется вызвать функцию play(Animator) несколько раз;
        * это вполне нормально*/
    }
}
/* ObjectAnimator называется аниматором свойства.Ничего не зная о том, как перемещать
* представление по экрану, аниматор свойства многократно вызывает сеттеры свойства с
* разными значениями. Объект ObjectAnimator создается вызовом функции
* ObjectAnimator.ofFloat(sunView, "y", 0, 1). При запуске ObjectAnimator функция
* sunView.setY(float) многократно вызывается с постепенно увеличивающимися значениями,
* начиная с 0 и так далее, пока не будет вызвана функция sunView.setY(1). Процесс
* вычисления значений между начальной и конечной точками называется интерполяцией. Между
* каждой интерполированной парой проходит небольшой промежуток времени; так создается
* иллюзия перемещения представления*/