package com.jatrenammapride.data.repository

import com.jatrenammapride.data.local.AppDatabase
import com.jatrenammapride.data.model.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class StoryRepository(
    private val database: AppDatabase
) {
    private val storyDao = database.storyDao()

    fun getStories(): Flow<List<Story>> {
        return storyDao.getAllStories()
    }

    suspend fun addStory(story: Story) {
        storyDao.insertStory(story)
    }

    suspend fun preloadSampleStoriesIfEmpty() {
        val existing = storyDao.getAllStories().first()
        if (existing.isNotEmpty()) return
        val samples = listOf(
            Story(
                title = "The Grand Chariot Procession",
                content = "Every year, the magnificent chariot procession marks the highlight of the jatre. " +
                        "Thousands of devotees gather to pull the towering ratha through the streets, " +
                        "accompanied by traditional music and chanting. The chariot, adorned with flowers " +
                        "and lights, makes its way from the temple to the main square, a journey that " +
                        "symbolises the deity's blessing upon the community.",
                jatreName = "Karaga Jatre"
            ),
            Story(
                title = "Wrestling Legends of the Jatre",
                content = "The wrestling arena has been a centrepiece of the jatre for over a century. " +
                        "Wrestlers from across the region compete for honour and glory in the traditional " +
                        "kusti matches. Elders recall legendary bouts that lasted hours, with champions " +
                        "earning respect that lasted a lifetime. Today, the tradition continues with " +
                        "young wrestlers carrying forward this proud heritage.",
                jatreName = "Dasara Jatre"
            ),
            Story(
                title = "The Sacred Night Lamp Ceremony",
                content = "On the final evening of the jatre, hundreds of oil lamps are lit along the " +
                        "temple pathway. Families bring their own lamps, each representing a prayer or " +
                        "wish. The sight of the glowing lamps reflected in the temple pond is a moment " +
                        "of serene beauty that remains etched in the memory of every visitor. This " +
                        "ceremony has been performed unbroken for over two hundred years.",
                jatreName = "Marikamba Jatre"
            ),
            Story(
                title = "Flavours of the Festival",
                content = "No jatre is complete without its legendary street food. From piping-hot " +
                        "mirchi bajji and sweet cotton candy to the famous local delicacy of karadantu, " +
                        "the food stalls offer a feast for every palate. Generations of families have " +
                        "run the same stalls, passing down recipes that taste of tradition and " +
                        "community spirit.",
                jatreName = "Yellamma Jatre"
            ),
            Story(
                title = "The Folk Theatre Tradition",
                content = "Bayalata, the open-air folk theatre, has been an integral part of the jatre " +
                        "since ancient times. Performers enact mythological stories under the stars, " +
                        "captivating audiences with elaborate costumes, dramatic dialogue, and live " +
                        "music. These performances preserve Karnataka's rich theatrical heritage and " +
                        "introduce young audiences to stories of their culture.",
                jatreName = "Banashankari Jatre"
            )
        )
        for (story in samples) {
            storyDao.insertStory(story)
        }
    }
}
