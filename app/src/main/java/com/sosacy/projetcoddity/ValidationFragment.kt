package com.sosacy.projetcoddity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.sosacy.projetcoddity.data.model.Garbage
import com.sosacy.projetcoddity.data.model.GarbageList
import com.sosacy.projetcoddity.databinding.FragmentValidationBinding
import com.sosacy.projetcoddity.web.WebClient
import com.yuyakaido.android.cardstackview.*
import com.yuyakaido.android.cardstackview.sample.CardStackAdapter
import com.yuyakaido.android.cardstackview.sample.GarbageDiffCallback
import java.util.ArrayList


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ValidationFragment : Fragment(), CardStackListener {
    private val hideHandler = Handler()

    private val TAG = ValidationFragment::class.java.simpleName


    @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        val flags =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        activity?.window?.decorView?.systemUiVisibility = flags
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        fullscreenContentControls?.visibility = View.VISIBLE
    }
    private var visible: Boolean = false
    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private var dummyButton: Button? = null
    private var fullscreenContent: View? = null
    private var fullscreenContentControls: View? = null

    private var _binding: FragmentValidationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreate(savedInstanceState)
        _binding = FragmentValidationBinding.inflate(inflater, container, false)

        return binding.root

    }

    var garbages = ArrayList<Garbage>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setContentView(R.layout.fragment_validation)
        //setupNavigation()
        WebClient(requireContext()).getGarbagesToRate() {
            var garbageList = GarbageList()
            garbageList.parseJson(it!!)
            garbages = garbageList.all
            if (garbages.size == 0) {
                Navigation.findNavController(this.requireView())
                    .navigate(R.id.action_validation_to_navigation_home)
            } else {
                setupCardStackView()
                setupButton()
                visible = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Clear the systemUiVisibility flag
        activity?.window?.decorView?.systemUiVisibility = 0
        show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dummyButton = null
        fullscreenContent = null
        fullscreenContentControls = null
    }

    private fun toggle() {
        if (visible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        fullscreenContentControls?.visibility = View.GONE
        visible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @Suppress("InlinedApi")
    private fun show() {
        // Show the system bar
        fullscreenContent?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        visible = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val drawerLayout by lazy { requireView().findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val cardStackView by lazy { requireView().findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this.requireContext(), this) }
    private val adapter by lazy { CardStackAdapter(garbages) }

    var nb = 0

    /*override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }*/

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        updateBtnView(nb + 1)

        var liveGarbage = adapter.getGarbages()[nb].id
        Log.d(TAG, "id card=" + liveGarbage)

        if (direction == Direction.Right) {
            WebClient(requireContext()).rateGarbage(liveGarbage, 1) {
                Log.d(TAG, "Card id=" + liveGarbage + " note=" + 1)
            }
        } else if (direction == Direction.Left) {
            WebClient(requireContext()).rateGarbage(liveGarbage, 0) {
                Log.d(TAG, "Card id=" + liveGarbage + " note=" + 0)
            }
        }

        Log.d(
            "CardStackView",
            "onCardSwiped nº$nb id=$liveGarbage p = ${manager.topPosition}, d = $direction"
        )
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
        nb++
        if (nb - 1 == adapter.getGarbages().size) {
            Navigation.findNavController(this.requireView())
                .navigate(R.id.action_validation_to_navigation_home)
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")

        updateBtnView(nb + 1)
        nb--
        var liveGarbage = adapter.getGarbages()[nb].id
        Log.d(TAG, "id card=" + liveGarbage)

    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
        var nbTotal = adapter.getGarbages().size

        Log.d(TAG, "Nb of Card($position) = $nb/$nbTotal")
    }

    /*private fun setupNavigation() {
        // Toolbar
        val toolbar = requireView().findViewById<Toolbar>(R.id.toolbar)

        // DrawerLayout
        val actionBarDrawerToggle = ActionBarDrawerToggle(requireView().this, drawerLayout, toolbar, com.yuyakaido.android.cardstackview.R.string.open_drawer, com.yuyakaido.android.cardstackview.R.string.close_drawer)
        actionBarDrawerToggle.syncState()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        // NavigationView
        /*val navigationView = requireView().findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                com.yuyakaido.android.cardstackview.R.id.reload -> reload()
                com.yuyakaido.android.cardstackview.R.id.add_spot_to_first -> addFirst(1)
                com.yuyakaido.android.cardstackview.R.id.add_spot_to_last -> addLast(1)
                com.yuyakaido.android.cardstackview.R.id.remove_spot_from_first -> removeFirst(1)
                com.yuyakaido.android.cardstackview.R.id.remove_spot_from_last -> removeLast(1)
                com.yuyakaido.android.cardstackview.R.id.replace_first_spot -> replace()
                com.yuyakaido.android.cardstackview.R.id.swap_first_for_last -> swap()
            }
            drawerLayout.closeDrawers()
            true
        }*/
    }*/

    private fun setupCardStackView() {
        initialize()
    }

    private fun updateBtnView(value: Int) {
        val rewind = requireView().findViewById<View>(R.id.rewind_button)
        if (value > 0) {
            rewind.setVisibility(View.VISIBLE);
        }
        if (value == 0) {
            rewind.setVisibility(View.INVISIBLE);
        }
        if (value == adapter.getGarbages().size) {
            Navigation.findNavController(this.requireView())
                .navigate(R.id.action_validation_to_navigation_home)
        }
    }

    private fun setupButton() {

        val rewind = requireView().findViewById<View>(R.id.rewind_button)
        rewind.setVisibility(View.INVISIBLE);

        val skip = requireView().findViewById<View>(R.id.skip_button)

        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()

            var liveGarbage = adapter.getGarbages()[nb].id - 1
            Log.d(TAG, "id card=" + liveGarbage)

            if (nb > 0) {
                rewind.setVisibility(View.VISIBLE);
            }
            if (nb == 0) {
                rewind.setVisibility(View.INVISIBLE);
            }
            if (nb == adapter.getGarbages().size) {
                Navigation.findNavController(this.requireView())
                    .navigate(R.id.action_validation_to_navigation_home)
            }


        }

        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()

            Log.d(TAG, "nb=" + nb)
            if (nb > 0) {
                rewind.setVisibility(View.VISIBLE);
            }
            if (nb == 0) {
                rewind.setVisibility(View.INVISIBLE);
            }
            if (nb == adapter.getGarbages().size) {
                Navigation.findNavController(this.requireView())
                    .navigate(R.id.action_validation_to_navigation_home)
            }
        }

        val like = requireView().findViewById<View>(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()

            Log.d(TAG, "nb=" + nb)

        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun paginate() {
        val old = adapter.getGarbages()
        val new = old.plus(createGarbages())
        val callback = GarbageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setGarbages(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun reload() {
        val old = adapter.getGarbages()
        val new = createGarbages()
        val callback = GarbageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setGarbages(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun addFirst(size: Int) {
        val old = adapter.getGarbages()
        val new = mutableListOf<Garbage>().apply {
            addAll(old)
            for (i in 0 until size) {
                add(manager.topPosition, createGarbage())
            }
        }
        val callback = GarbageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setGarbages(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun addLast(size: Int) {
        val old = adapter.getGarbages()
        val new = mutableListOf<Garbage>().apply {
            addAll(old)
            addAll(List(size) { createGarbage() })
        }
        val callback = GarbageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setGarbages(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeFirst(size: Int) {
        if (adapter.getGarbages().isEmpty()) {
            return
        }

        val old = adapter.getGarbages()
        val new = mutableListOf<Garbage>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(manager.topPosition)
            }
        }
        val callback = GarbageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setGarbages(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeLast(size: Int) {
        if (adapter.getGarbages().isEmpty()) {
            return
        }

        val old = adapter.getGarbages()
        val new = mutableListOf<Garbage>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(this.size - 1)
            }
        }
        val callback = GarbageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setGarbages(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun replace() {
        val old = adapter.getGarbages()
        val new = mutableListOf<Garbage>().apply {
            addAll(old)
            removeAt(manager.topPosition)
            add(manager.topPosition, createGarbage())
        }
        adapter.setGarbages(new)
        adapter.notifyItemChanged(manager.topPosition)
    }

    private fun swap() {
        val old = adapter.getGarbages()
        val new = mutableListOf<Garbage>().apply {
            addAll(old)
            val first = removeAt(manager.topPosition)
            val last = removeAt(this.size - 1)
            add(manager.topPosition, last)
            add(first)
        }
        val callback = GarbageDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setGarbages(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun createGarbage(): Garbage {
        return Garbage(
            id = 4,
            latitude = 444F,
            longitude = 676F,
            discard = false,
            accepted = false
        )
    }

    private fun createGarbages(): List<Garbage> {
        val garbages = ArrayList<Garbage>()
        garbages.add(
            Garbage(
                id = 1,
                latitude = 111F,
                longitude = 000F,
                discard = false,
                accepted = false
            )
        )
        garbages.add(
            Garbage(
                id = 2,
                latitude = 222F,
                longitude = 888F,
                discard = false,
                accepted = false
            )
        )
        garbages.add(
            Garbage(
                id = 3,
                latitude = 333F,
                longitude = 999F,
                discard = false,
                accepted = false
            )
        )
        return garbages
    }
}