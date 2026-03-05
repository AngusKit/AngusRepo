import { useState } from 'react';
import { 
  BarChart3, TrendingUp, Download, Upload, HardDrive, Users,
  Calendar, Package, ArrowUpRight, ArrowDownRight, Clock,
  Filter, FileText, Share2, Search
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { repositories } from '@/data/repositories';
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
} from '@/components/ui/command';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';

export function Analytics() {
  const { t } = useLanguage();
  const [timeRange, setTimeRange] = useState('7days');
  const [selectedRepository, setSelectedRepository] = useState('all');
  const [repoSearchOpen, setRepoSearchOpen] = useState(false);

  // Mock data
  const downloadData = [
    { date: '01/12', downloads: 1200 },
    { date: '01/13', downloads: 1580 },
    { date: '01/14', downloads: 1420 },
    { date: '01/15', downloads: 1890 },
    { date: '01/16', downloads: 2100 },
    { date: '01/17', downloads: 1950 },
    { date: '01/18', downloads: 2340 }
  ];

  const topArtifacts = [
    { name: 'spring-core', repository: 'maven-releases', downloads: 125430, change: 12 },
    { name: 'nginx', repository: 'docker-registry', downloads: 567892, change: -5 },
    { name: 'react', repository: 'npm-public', downloads: 2341567, change: 23 },
    { name: 'pandas', repository: 'pypi-public', downloads: 892345, change: 8 },
    { name: 'lodash', repository: 'npm-public', downloads: 5678901, change: 15 }
  ];

  const topUsers = [
    { name: 'john.doe@company.com', downloads: 1234, uploads: 45 },
    { name: 'jane.smith@startup.io', downloads: 987, uploads: 32 },
    { name: 'bob.wilson@enterprise.com', downloads: 756, uploads: 28 },
    { name: 'alice.johnson@tech.com', downloads: 645, uploads: 19 },
    { name: 'charlie.brown@dev.io', downloads: 523, uploads: 15 }
  ];

  const repositoryStats = [
    { name: 'maven-releases', artifacts: 156, size: '45.2 GB', downloads: 125430 },
    { name: 'docker-registry', artifacts: 45, size: '234.5 GB', downloads: 567892 },
    { name: 'npm-public', artifacts: 289, size: '12.8 GB', downloads: 7920468 },
    { name: 'pypi-public', artifacts: 78, size: '28.4 GB', downloads: 892345 }
  ];

  const handleExport = (format: string) => {
    toast.success(t('analytics.exportSuccess', { format: format.toUpperCase() }));
  };

  return (
    <div className="flex flex-col h-[calc(100vh-8rem)]">
      {/* Header */}
      <div className="mb-6">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-3xl text-gray-900 dark:text-white mb-2">
              {t('analytics.title')}
            </h1>
            <p className="text-gray-600 dark:text-gray-400">
              {t('analytics.description')}
            </p>
          </div>
          <div className="flex items-center gap-2">
            <Popover open={repoSearchOpen} onOpenChange={setRepoSearchOpen}>
              <PopoverTrigger asChild>
                <Button variant="outline" className="w-56 justify-between">
                  <span className="truncate">
                    {selectedRepository === 'all' 
                      ? t('artifacts.allRepositories')
                      : repositories.find(r => r.id === selectedRepository)?.name || t('artifacts.allRepositories')
                    }
                  </span>
                  <Search className="ml-2 size-4 shrink-0 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-56 p-0" align="start">
                <Command>
                  <CommandInput placeholder={t('repositories.searchPlaceholder')} />
                  <CommandEmpty>{t('common.noData')}</CommandEmpty>
                  <CommandGroup className="max-h-64 overflow-auto">
                    <CommandItem
                      value="all"
                      onSelect={() => {
                        setSelectedRepository('all');
                        setRepoSearchOpen(false);
                      }}
                    >
                      {t('artifacts.allRepositories')}
                    </CommandItem>
                    {repositories.map((repo) => (
                      <CommandItem
                        key={repo.id}
                        value={repo.name}
                        onSelect={() => {
                          setSelectedRepository(repo.id);
                          setRepoSearchOpen(false);
                        }}
                      >
                        <Package className="mr-2 size-4" />
                        <span className="truncate">{repo.name}</span>
                        <Badge className="ml-auto text-xs" variant="outline">
                          {repo.format}
                        </Badge>
                      </CommandItem>
                    ))}
                  </CommandGroup>
                </Command>
              </PopoverContent>
            </Popover>
            <Select value={timeRange} onValueChange={setTimeRange}>
              <SelectTrigger className="w-48">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="today">{t('analytics.today')}</SelectItem>
                <SelectItem value="7days">{t('analytics.last7Days')}</SelectItem>
                <SelectItem value="30days">{t('analytics.last30Days')}</SelectItem>
                <SelectItem value="90days">{t('analytics.last90Days')}</SelectItem>
              </SelectContent>
            </Select>
            <Button variant="outline" onClick={() => handleExport('csv')}>
              <FileText className="mr-2 size-4" />
              {t('analytics.exportData')}
            </Button>
          </div>
        </div>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-4 gap-4 mb-6">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-gray-600 dark:text-gray-400">{t('analytics.totalDownloads')}</p>
              <Download className="size-8 text-blue-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-gray-900 dark:text-white">9.8M</p>
            <div className="flex items-center gap-1 mt-2">
              <ArrowUpRight className="size-4 text-green-600 dark:text-green-400" />
              <p className="text-sm text-green-600 dark:text-green-400">+12.5% {t('analytics.vsLastWeek')}</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-gray-600 dark:text-gray-400">{t('analytics.totalUploads')}</p>
              <Upload className="size-8 text-green-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-gray-900 dark:text-white">1,234</p>
            <div className="flex items-center gap-1 mt-2">
              <ArrowUpRight className="size-4 text-green-600 dark:text-green-400" />
              <p className="text-sm text-green-600 dark:text-green-400">+8.3% {t('analytics.vsLastWeek')}</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-gray-600 dark:text-gray-400">{t('analytics.storageUsed')}</p>
              <HardDrive className="size-8 text-purple-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-gray-900 dark:text-white">320.9 GB</p>
            <div className="flex items-center gap-1 mt-2">
              <ArrowUpRight className="size-4 text-orange-600 dark:text-orange-400" />
              <p className="text-sm text-orange-600 dark:text-orange-400">+5.2 GB {t('analytics.thisWeek')}</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-gray-600 dark:text-gray-400">{t('analytics.activeUsers')}</p>
              <Users className="size-8 text-indigo-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-gray-900 dark:text-white">287</p>
            <div className="flex items-center gap-1 mt-2">
              <ArrowDownRight className="size-4 text-red-600 dark:text-red-400" />
              <p className="text-sm text-red-600 dark:text-red-400">-3.2% {t('analytics.vsLastWeek')}</p>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Charts and Tables */}
      <div className="flex-1 grid grid-cols-3 gap-6 min-h-0">
        {/* Left Panel - Download Trend */}
        <div className="col-span-2 flex flex-col space-y-6">
          <Card className="flex-1">
            <CardHeader>
              <CardTitle>{t('analytics.downloadTrend')}</CardTitle>
              <CardDescription>{t('analytics.downloadTrendDesc')}</CardDescription>
            </CardHeader>
            <CardContent className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={downloadData} margin={{ top: 5, right: 10, left: -20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" className="stroke-gray-200 dark:stroke-gray-700" />
                  <XAxis 
                    dataKey="date" 
                    className="text-xs fill-gray-600 dark:fill-gray-400"
                    tick={{ fontSize: 12 }}
                  />
                  <YAxis 
                    className="text-xs fill-gray-600 dark:fill-gray-400"
                    tick={{ fontSize: 12 }}
                  />
                  <Tooltip 
                    contentStyle={{
                      backgroundColor: 'rgba(255, 255, 255, 0.95)',
                      border: '1px solid #e5e7eb',
                      borderRadius: '6px',
                      fontSize: '12px'
                    }}
                    labelStyle={{ fontWeight: 'bold', marginBottom: '4px' }}
                  />
                  <Line 
                    type="monotone" 
                    dataKey="downloads" 
                    stroke="#3b82f6" 
                    strokeWidth={2}
                    dot={{ fill: '#3b82f6', r: 4 }}
                    activeDot={{ r: 6 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          {/* Repository Stats */}
          <Card>
            <CardHeader>
              <CardTitle>{t('analytics.repositoryStats')}</CardTitle>
              <CardDescription>{t('analytics.repositoryStatsDesc')}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {repositoryStats.map((repo, index) => (
                  <div key={index} className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
                    <div className="flex items-center gap-3">
                      <div className="size-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                        <Package className="size-5 text-blue-600 dark:text-blue-400" />
                      </div>
                      <div>
                        <p className="font-medium text-gray-900 dark:text-white">{repo.name}</p>
                        <p className="text-sm text-gray-600 dark:text-gray-400">
                          {repo.artifacts} {t('analytics.artifacts')} • {repo.size}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="font-semibold text-gray-900 dark:text-white">
                        {repo.downloads.toLocaleString()}
                      </p>
                      <p className="text-sm text-gray-600 dark:text-gray-400">
                        {t('analytics.downloads')}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Panel - Top Lists */}
        <div className="col-span-1 flex flex-col space-y-6">
          {/* Top Artifacts */}
          <Card className="flex-1">
            <CardHeader>
              <CardTitle>{t('analytics.topArtifacts')}</CardTitle>
              <CardDescription>{t('analytics.mostDownloaded')}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {topArtifacts.map((artifact, index) => (
                  <div key={index} className="flex items-center gap-3">
                    <div className="size-8 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center flex-shrink-0">
                      <span className="text-sm font-semibold text-gray-600 dark:text-gray-400">
                        {index + 1}
                      </span>
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="font-medium text-gray-900 dark:text-white truncate">
                        {artifact.name}
                      </p>
                      <p className="text-sm text-gray-600 dark:text-gray-400">
                        {(artifact.downloads / 1000).toFixed(0)}K {t('analytics.downloads')}
                      </p>
                    </div>
                    <div className={`flex items-center gap-1 text-sm ${
                      artifact.change > 0 
                        ? 'text-green-600 dark:text-green-400' 
                        : 'text-red-600 dark:text-red-400'
                    }`}>
                      {artifact.change > 0 ? (
                        <ArrowUpRight className="size-4" />
                      ) : (
                        <ArrowDownRight className="size-4" />
                      )}
                      <span>{Math.abs(artifact.change)}%</span>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* Top Users */}
          <Card className="flex-1">
            <CardHeader>
              <CardTitle>{t('analytics.topUsers')}</CardTitle>
              <CardDescription>{t('analytics.mostActive')}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {topUsers.map((user, index) => (
                  <div key={index} className="flex items-center gap-3">
                    <div className="size-8 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center flex-shrink-0 text-white text-sm font-semibold">
                      {user.name.charAt(0).toUpperCase()}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="font-medium text-gray-900 dark:text-white truncate text-sm">
                        {user.name}
                      </p>
                      <p className="text-xs text-gray-600 dark:text-gray-400">
                        {user.downloads} {t('analytics.downloads')} • {user.uploads} {t('analytics.uploads')}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}