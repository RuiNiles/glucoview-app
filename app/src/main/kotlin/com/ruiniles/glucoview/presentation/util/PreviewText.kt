package com.ruiniles.glucoview.presentation.util


object PreviewText {

    val GOOGLE_SERVICE_JSON = """
            {
              "project_info": {
                "project_number": "123456789012",
                "project_id": "glucoview",
                "storage_bucket": "glucoview.firebasestorage.app"
              },
              "client": [
                {
                  "client_info": {
                    "mobilesdk_app_id": "app-id-preview-text",
                    "android_client_info": {
                      "package_name": "com.ruiniles.glucoview"
                    }
                  },
                  "oauth_client": [],
                  "api_key": [
                    {
                      "current_key": "key-preview-text"
                    }
                  ],
                  "services": {
                    "appinvite_service": {
                      "other_platform_oauth_client": []
                    }
                  }
                }
              ],
              "configuration_version": "1"
            }
        """.trimIndent()
}
