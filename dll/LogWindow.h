#pragma once

void CreateLogWindow();
void CloseLogWindow();
bool IsLogWindowCreated();

void LogInfo(const char* message);
void LogWarning(const char* message);
void LogError(const char* message);
void LogDebug(const char* message);
