package com.funsol.funmob_sdk.events

import com.funsol.funmob_sdk.model.CampaignResponse

sealed class CampaignResponseEvent {
    class Success(val campaignResponse: CampaignResponse) : CampaignResponseEvent()
    class Failure(val error: String) : CampaignResponseEvent()
    object Loading : CampaignResponseEvent()
    object Empty : CampaignResponseEvent()
}

