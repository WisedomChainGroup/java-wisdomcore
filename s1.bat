for($i=0;$i -le 7;$i++)
{
 Start-Process -FilePath godotenv.exe  ".\gradlew .\local$i.env .\gradlew run"
}