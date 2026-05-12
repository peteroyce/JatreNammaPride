package com.jatrenammapride.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.jatrenammapride.data.model.Event
import com.jatrenammapride.data.model.LostItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Firebase-backed data source with graceful fallback to local in-memory data.
 *
 * Attempts to use Firestore and Firebase Storage for real-time sync.
 * If Firebase is not configured (no google-services.json or unreachable backend),
 * falls back transparently to local sample data so the app still works in demo mode.
 */
class FirebaseDataSource {

    companion object {
        private const val TAG = "FirebaseDataSource"
        private const val EVENTS_COLLECTION = "events"
        private const val LOST_ITEMS_COLLECTION = "lost_items"
        private const val STORAGE_IMAGES_PATH = "images"
    }

    // ── Firebase instances (lazy so they don't crash if unconfigured) ────

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (isFirebaseAvailable()) FirebaseFirestore.getInstance() else null
        } catch (e: Exception) {
            Log.w(TAG, "Firestore unavailable, using local data", e)
            null
        }
    }

    private val storage: FirebaseStorage? by lazy {
        try {
            if (isFirebaseAvailable()) FirebaseStorage.getInstance() else null
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Storage unavailable, using placeholder URLs", e)
            null
        }
    }

    // ── Local fallback stores ────────────────────────────────────────────

    private val localEventsFlow = MutableStateFlow(sampleEvents())
    private val localLostItemsFlow = MutableStateFlow(sampleLostItems())

    // ── Firebase availability check ─────────────────────────────────────

    /**
     * Returns true if a FirebaseApp has been successfully initialised.
     * This will be false when google-services.json is missing or invalid.
     */
    private fun isFirebaseAvailable(): Boolean {
        return try {
            FirebaseApp.getInstance() != null
        } catch (e: IllegalStateException) {
            false
        }
    }

    // ── Events ──────────────────────────────────────────────────────────

    /**
     * Observe the events collection in real-time.
     * Uses Firestore snapshot listener when available, otherwise emits local data.
     */
    fun observeEvents(): Flow<List<Event>> {
        val db = firestore
        if (db != null) {
            return callbackFlow {
                val registration: ListenerRegistration = db.collection(EVENTS_COLLECTION)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e(TAG, "Error observing events, falling back to local", error)
                            trySend(localEventsFlow.value)
                            return@addSnapshotListener
                        }
                        val events = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                Event(
                                    title = doc.getString("title") ?: "",
                                    description = doc.getString("description") ?: "",
                                    startTime = doc.getLong("startTime") ?: 0L,
                                    endTime = doc.getLong("endTime") ?: 0L,
                                    location = doc.getString("location") ?: "",
                                    category = doc.getString("category") ?: "",
                                    status = doc.getString("status") ?: "Upcoming",
                                    imageUrl = doc.getString("imageUrl"),
                                    firebaseKey = doc.id
                                )
                            } catch (e: Exception) {
                                Log.w(TAG, "Failed to parse event doc ${doc.id}", e)
                                null
                            }
                        } ?: emptyList()
                        trySend(events)
                    }
                awaitClose { registration.remove() }
            }
        }
        // Fallback: return local in-memory data
        Log.d(TAG, "Firebase unavailable — serving local event data")
        return localEventsFlow.asStateFlow()
    }

    /**
     * Add a new event to Firestore. Falls back to local list on failure.
     */
    fun addEvent(event: Event) {
        val db = firestore
        if (db != null) {
            val data = hashMapOf(
                "title" to event.title,
                "description" to event.description,
                "startTime" to event.startTime,
                "endTime" to event.endTime,
                "location" to event.location,
                "category" to event.category,
                "status" to event.status,
                "imageUrl" to event.imageUrl,
                "createdAt" to event.createdAt
            )
            db.collection(EVENTS_COLLECTION)
                .add(data)
                .addOnSuccessListener { ref ->
                    Log.d(TAG, "Event added with key: ${ref.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to add event to Firestore, saving locally", e)
                    addEventLocally(event)
                }
        } else {
            addEventLocally(event)
        }
    }

    private fun addEventLocally(event: Event) {
        val list = localEventsFlow.value.toMutableList()
        list.add(event)
        localEventsFlow.value = list
    }

    /**
     * Update an existing event in Firestore by its document key.
     */
    fun updateEvent(key: String, event: Event) {
        val db = firestore
        if (db != null) {
            val data = hashMapOf<String, Any>(
                "title" to event.title,
                "description" to event.description,
                "startTime" to event.startTime,
                "endTime" to event.endTime,
                "location" to event.location,
                "category" to event.category,
                "status" to event.status,
                "createdAt" to event.createdAt
            )
            event.imageUrl?.let { data["imageUrl"] = it }

            db.collection(EVENTS_COLLECTION).document(key)
                .update(data)
                .addOnSuccessListener { Log.d(TAG, "Event $key updated") }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to update event $key", e)
                    updateEventLocally(key, event)
                }
        } else {
            updateEventLocally(key, event)
        }
    }

    private fun updateEventLocally(key: String, event: Event) {
        localEventsFlow.value = localEventsFlow.value.map {
            if (it.firebaseKey == key) event.copy(firebaseKey = key) else it
        }
    }

    /**
     * Delete an event from Firestore by its document key.
     */
    fun deleteEvent(key: String) {
        val db = firestore
        if (db != null) {
            db.collection(EVENTS_COLLECTION).document(key)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Event $key deleted") }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to delete event $key", e)
                    localEventsFlow.value = localEventsFlow.value.filter { it.firebaseKey != key }
                }
        } else {
            localEventsFlow.value = localEventsFlow.value.filter { it.firebaseKey != key }
        }
    }

    // ── Lost Items ──────────────────────────────────────────────────────

    /**
     * Observe the lost_items collection in real-time.
     * Uses Firestore snapshot listener when available, otherwise emits local data.
     */
    fun observeLostItems(): Flow<List<LostItem>> {
        val db = firestore
        if (db != null) {
            return callbackFlow {
                val registration: ListenerRegistration = db.collection(LOST_ITEMS_COLLECTION)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e(TAG, "Error observing lost items, falling back to local", error)
                            trySend(localLostItemsFlow.value)
                            return@addSnapshotListener
                        }
                        val items = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                LostItem(
                                    type = doc.getString("type") ?: "Lost",
                                    description = doc.getString("description") ?: "",
                                    photoUrl = doc.getString("photoUrl") ?: "",
                                    contactInfo = doc.getString("contactInfo") ?: "",
                                    lastSeenLocation = doc.getString("lastSeenLocation") ?: "",
                                    isResolved = doc.getBoolean("isResolved") ?: false,
                                    firebaseKey = doc.id
                                )
                            } catch (e: Exception) {
                                Log.w(TAG, "Failed to parse lost item doc ${doc.id}", e)
                                null
                            }
                        } ?: emptyList()
                        trySend(items)
                    }
                awaitClose { registration.remove() }
            }
        }
        // Fallback: return local in-memory data
        Log.d(TAG, "Firebase unavailable — serving local lost-item data")
        return localLostItemsFlow.asStateFlow()
    }

    /**
     * Add a new lost/found item to Firestore.
     */
    fun addLostItem(item: LostItem) {
        val db = firestore
        if (db != null) {
            val data = hashMapOf(
                "type" to item.type,
                "description" to item.description,
                "photoUrl" to item.photoUrl,
                "contactInfo" to item.contactInfo,
                "lastSeenLocation" to item.lastSeenLocation,
                "isResolved" to item.isResolved,
                "createdAt" to item.createdAt
            )
            db.collection(LOST_ITEMS_COLLECTION)
                .add(data)
                .addOnSuccessListener { ref ->
                    Log.d(TAG, "Lost item added with key: ${ref.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to add lost item to Firestore, saving locally", e)
                    addLostItemLocally(item)
                }
        } else {
            addLostItemLocally(item)
        }
    }

    private fun addLostItemLocally(item: LostItem) {
        val list = localLostItemsFlow.value.toMutableList()
        list.add(item)
        localLostItemsFlow.value = list
    }

    /**
     * Update fields on a lost item (e.g. marking it resolved).
     */
    fun updateLostItem(key: String, updates: Map<String, Any>) {
        val db = firestore
        if (db != null) {
            db.collection(LOST_ITEMS_COLLECTION).document(key)
                .update(updates)
                .addOnSuccessListener { Log.d(TAG, "Lost item $key updated") }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to update lost item $key", e)
                    updateLostItemLocally(key, updates)
                }
        } else {
            updateLostItemLocally(key, updates)
        }
    }

    private fun updateLostItemLocally(key: String, updates: Map<String, Any>) {
        localLostItemsFlow.value = localLostItemsFlow.value.map { item ->
            if (item.firebaseKey == key) {
                var updated = item
                updates["isResolved"]?.let { updated = updated.copy(isResolved = it as Boolean) }
                updated
            } else item
        }
    }

    // ── Image Upload ────────────────────────────────────────────────────

    /**
     * Upload an image to Firebase Storage and return its download URL.
     * Falls back to a placeholder URL if Storage is unavailable.
     */
    suspend fun uploadImage(uri: Uri): String {
        val storageInstance = storage
        if (storageInstance != null) {
            return try {
                val fileName = "${UUID.randomUUID()}.jpg"
                val ref = storageInstance.reference
                    .child(STORAGE_IMAGES_PATH)
                    .child(fileName)
                ref.putFile(uri).await()
                val downloadUrl = ref.downloadUrl.await()
                Log.d(TAG, "Image uploaded: $downloadUrl")
                downloadUrl.toString()
            } catch (e: Exception) {
                Log.e(TAG, "Image upload failed, returning placeholder URL", e)
                "https://placeholder.example.com/images/${UUID.randomUUID()}.jpg"
            }
        }
        // Fallback: return a placeholder URL
        return "https://placeholder.example.com/images/${UUID.randomUUID()}.jpg"
    }

    // ── Sample Data (used as fallback when Firebase is not configured) ──

    private fun sampleEvents(): List<Event> {
        val now = System.currentTimeMillis()
        val hour = 3_600_000L
        return listOf(
            Event(
                title = "Rathotsava Procession",
                description = "The grand chariot procession through the main streets. Devotees pull the towering ratha adorned with flowers and lights.",
                startTime = now + 2 * hour,
                endTime = now + 5 * hour,
                location = "Temple Main Road",
                category = "Religious",
                status = "Upcoming",
                firebaseKey = "evt_1"
            ),
            Event(
                title = "Wrestling Championship",
                description = "Traditional kusti matches featuring wrestlers from across the region competing for honour and glory.",
                startTime = now + 6 * hour,
                endTime = now + 9 * hour,
                location = "Central Arena",
                category = "Sports",
                status = "Upcoming",
                firebaseKey = "evt_2"
            ),
            Event(
                title = "Folk Dance Performances",
                description = "Vibrant Yakshagana and Dollu Kunitha performances by renowned troupes from across Karnataka.",
                startTime = now + 10 * hour,
                endTime = now + 13 * hour,
                location = "Main Stage",
                category = "Cultural",
                status = "Upcoming",
                firebaseKey = "evt_3"
            ),
            Event(
                title = "Food Festival",
                description = "Savour local delicacies — mirchi bajji, jolada rotti, karadantu, cotton candy, and more from 30+ stalls.",
                startTime = now + 1 * hour,
                endTime = now + 14 * hour,
                location = "Food Stalls Area",
                category = "Food",
                status = "Ongoing",
                firebaseKey = "evt_4"
            ),
            Event(
                title = "Closing Ceremony & Lamp Lighting",
                description = "Hundreds of oil lamps illuminate the temple pathway as the jatre concludes with prayers and community gathering.",
                startTime = now + 24 * hour,
                endTime = now + 27 * hour,
                location = "Temple Pond",
                category = "Religious",
                status = "Upcoming",
                firebaseKey = "evt_5"
            )
        )
    }

    private fun sampleLostItems(): List<LostItem> {
        return listOf(
            LostItem(
                type = "Lost",
                description = "Brown leather wallet with ID cards and some cash. Lost near the food stalls area around noon.",
                photoUrl = "",
                contactInfo = "9876543210",
                lastSeenLocation = "Food Stalls Area",
                firebaseKey = "lost_1"
            ),
            LostItem(
                type = "Found",
                description = "Black Samsung phone found on a bench near the main stage. Screen lock is on.",
                photoUrl = "",
                contactInfo = "9123456780",
                lastSeenLocation = "Main Stage",
                firebaseKey = "lost_2"
            )
        )
    }
}
