import pandas as pd

# Load the Excel file
file_path = "TIMETABLE - II SEMESTER 2024 -25_removed.xlsx"
df = pd.read_excel(file_path, sheet_name="Table 1")

# Set proper column names from the first row and drop it
df.columns = df.iloc[0]
df = df[1:]

# Rename for easier access
df = df.rename(columns={
    'COMP CODE': 'Course ID',
    'COURSE NO.': 'COURSE NO',
    'INSTRUCTOR_IN_CHARGE/I NSTRUCTOR': 'INSTRUCTOR'
})

# Keep only the relevant columns
df_relevant = df[['COURSE NO', 'Course ID', 'INSTRUCTOR']].copy()

# Fill down COURSE NO and Course ID to associate each instructor
df_relevant[['COURSE NO', 'Course ID']] = df_relevant[['COURSE NO', 'Course ID']].fillna(method='ffill')

# Group by COURSE NO and Course ID, and collect all unique instructor names
grouped = df_relevant.groupby(['COURSE NO', 'Course ID'])['INSTRUCTOR'].apply(
    lambda x: ', '.join(pd.Series(x.dropna().unique()))
).reset_index()

# Save to CSV
grouped.to_csv("Course.csv", index=False)

print("✅ Course.csv has been created successfully.")
