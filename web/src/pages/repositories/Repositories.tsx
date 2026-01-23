import { useState } from 'react';
import { 
  Package, Plus, Search, MoreVertical, Settings, Trash2, Copy, Check,
  LayoutGrid, List, Download, RefreshCw, Activity, CheckCircle,
  XCircle, Clock, HardDrive, Shield, AlertTriangle,
  ChevronLeft, ChevronRight, BarChart3, Users, Archive, Globe, Eye, EyeOff
} from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { useLanguage } from '@/components/LanguageProvider';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger, DropdownMenuLabel } from '@/components/ui/dropdown-menu';
import { toast } from 'sonner';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Checkbox } from '@/components/ui/checkbox';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { copyToClipboard } from '@/utils/clipboard';
import { repositories, Repository, RepositoryStatus } from '@/data/repositories';
import { useNavigate } from 'react-router-dom';

type ViewMode = 'grid' | 'list';

// interface RepositoriesProps {
//   onCreate: () => void;
//   onViewDetail: (id: string) => void;
//   onEdit: (id: string) => void;
// }

export function Repositories() {
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [filterType, setFilterType] = useState('all');
  const [filterFormat, setFilterFormat] = useState('all');
  const [filterStatus, setFilterStatus] = useState('all');
  const [viewMode, setViewMode] = useState<ViewMode>('grid');
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [bulkDeleteDialogOpen, setBulkDeleteDialogOpen] = useState(false);
  const [selectedRepo, setSelectedRepo] = useState<Repository | null>(null);
  const [copiedUrl, setCopiedUrl] = useState<string | null>(null);
  const [selectedRepos, setSelectedRepos] = useState<string[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const onCreate = () => {
    navigate('/repositories/create');
  };

  const onViewDetail = (id: string) => {
    navigate(`/repositories/detail/${id}`);
  };

  const onEdit = (id: string) => {
    navigate(`/repositories/configure/${id}`);
  };

  const formatBadgeColor = (format: string) => {
    const colors: Record<string, string> = {
      'Maven': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
      'Docker': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      'NPM': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
      'PyPI': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-300',
      'NuGet': 'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-300',
      'APT': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
      'YUM': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
      'Helm': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      'Go': 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300',
      'Raw': 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300',
    };
    return colors[format] || 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
  };

  const typeBadgeColor = (type: string) => {
    const colors: Record<string, string> = {
      'hosted': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      'proxy': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
      'group': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
      'virtual': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
    };
    return colors[type] || 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
  };

  const getStatusIcon = (status: RepositoryStatus) => {
    switch (status) {
      case 'online':
        return <CheckCircle className="size-4 text-green-500" />;
      case 'offline':
        return <XCircle className="size-4 text-gray-400" />;
      default:
        return <Activity className="size-4 text-gray-400" />;
    }
  };

  const getHealthColor = (score: number) => {
    if (score >= 95) return 'text-green-600 dark:text-green-400';
    if (score >= 80) return 'text-yellow-600 dark:text-yellow-400';
    return 'text-red-600 dark:text-red-400';
  };

  const filteredRepositories = repositories.filter(repo => {
    const matchesSearch = repo.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         repo.description.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesType = filterType === 'all' || repo.type === filterType;
    const matchesFormat = filterFormat === 'all' || repo.format === filterFormat;
    const matchesStatus = filterStatus === 'all' || repo.status === filterStatus;
    return matchesSearch && matchesType && matchesFormat && matchesStatus;
  });

  // Pagination
  const totalPages = Math.ceil(filteredRepositories.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const paginatedRepositories = filteredRepositories.slice(startIndex, endIndex);

  const handleCopyUrl = async (url: string, id: string) => {
    const success = await copyToClipboard(url);
    if (success) {
      setCopiedUrl(id);
      toast.success(t('artifacts.urlCopied'));
      setTimeout(() => setCopiedUrl(null), 2000);
    } else {
      toast.error('Failed to copy URL');
    }
  };

  const handleDeleteRepository = (repo: Repository) => {
    setSelectedRepo(repo);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = () => {
    if (selectedRepo) {
      toast.success(t('repositories.repositoryDeleted'));
      setDeleteDialogOpen(false);
      setSelectedRepo(null);
    }
  };

  const handleBulkDelete = () => {
    setBulkDeleteDialogOpen(true);
  };

  const confirmBulkDelete = () => {
    toast.success(`${selectedRepos.length} ${t('repositories.repositoriesDeleted')}`);
    setSelectedRepos([]);
    setBulkDeleteDialogOpen(false);
  };

  const handleToggleRepoSelection = (id: string) => {
    setSelectedRepos(prev =>
      prev.includes(id) ? prev.filter(rid => rid !== id) : [...prev, id]
    );
  };

  const handleSelectAll = () => {
    if (selectedRepos.length === paginatedRepositories.length) {
      setSelectedRepos([]);
    } else {
      setSelectedRepos(paginatedRepositories.map(r => r.id));
    }
  };

  const handleSecurityScan = (repo: Repository) => {
    toast.success(t('repositories.scanStarted'));
  };

  const handleAnalyze = () => {
    toast.success(t('repositories.analysisStarted'));
  };

  const handleExport = (repo: Repository) => {
    toast.success(t('repositories.repositoryExported'));
  };

  const handleTogglePublic = (repo: Repository) => {
    toast.success(t('repositories.accessUpdated'));
  };

  const totalStats = {
    repositories: repositories.length,
    artifacts: repositories.reduce((sum, r) => sum + r.artifacts, 0),
    storage: repositories.reduce((sum, r) => sum + r.sizeBytes, 0),
    downloads: repositories.reduce((sum, r) => sum + r.stats.downloads, 0),
  };

  const formatBytes = (bytes: number) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
    if (bytes < 1024 * 1024 * 1024 * 1024) return (bytes / (1024 * 1024 * 1024)).toFixed(1) + ' GB';
    return (bytes / (1024 * 1024 * 1024 * 1024)).toFixed(1) + ' TB';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-3xl text-gray-900 dark:text-white mb-2">{t('repositories.title')}</h1>
          <p className="text-gray-600 dark:text-gray-400">{t('repositories.description')}</p>
        </div>
      </div>

      {/* Statistics Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.totalRepositories')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {totalStats.repositories}
                </p>
              </div>
              <div className="size-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                <Package className="size-6 text-blue-600 dark:text-blue-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.totalArtifacts')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {totalStats.artifacts.toLocaleString()}
                </p>
              </div>
              <div className="size-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                <Archive className="size-6 text-green-600 dark:text-green-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.totalStorage')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {formatBytes(totalStats.storage)}
                </p>
              </div>
              <div className="size-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                <HardDrive className="size-6 text-purple-600 dark:text-purple-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.totalDownloads')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {totalStats.downloads.toLocaleString()}
                </p>
              </div>
              <div className="size-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
                <Download className="size-6 text-orange-600 dark:text-orange-400" />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Search, Filters and View Toggle */}
      <div className="flex flex-col lg:flex-row gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
          <Input
            placeholder={t('repositories.searchPlaceholder')}
            value={searchQuery}
            onChange={(e) => {
              setSearchQuery(e.target.value);
              setCurrentPage(1);
            }}
            className="pl-10"
          />
        </div>
        <div className="flex items-center gap-2 flex-wrap">
          <Select value={filterType} onValueChange={(v) => { setFilterType(v); setCurrentPage(1); }}>
            <SelectTrigger className="w-full sm:w-36">
              <SelectValue placeholder={t('common.type')} />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">{t('repositories.allTypes')}</SelectItem>
              <SelectItem value="hosted">{t('repositories.hosted')}</SelectItem>
              <SelectItem value="proxy">{t('repositories.proxy')}</SelectItem>
              <SelectItem value="group">{t('repositories.group')}</SelectItem>
            </SelectContent>
          </Select>
          
          <Select value={filterFormat} onValueChange={(v) => { setFilterFormat(v); setCurrentPage(1); }}>
            <SelectTrigger className="w-full sm:w-36">
              <SelectValue placeholder={t('common.format')} />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">{t('repositories.allFormats')}</SelectItem>
              <SelectItem value="Maven">Maven</SelectItem>
              <SelectItem value="Docker">Docker</SelectItem>
              <SelectItem value="NPM">NPM</SelectItem>
              <SelectItem value="NuGet">NuGet</SelectItem>
              <SelectItem value="PyPI">PyPI</SelectItem>
              <SelectItem value="APT">APT</SelectItem>
              <SelectItem value="YUM">YUM</SelectItem>
              <SelectItem value="Helm">Helm</SelectItem>
              <SelectItem value="Go">Go</SelectItem>
              <SelectItem value="Raw">Raw</SelectItem>
            </SelectContent>
          </Select>

          <div className="flex items-center border border-gray-200 dark:border-gray-700 rounded-lg">
            <Button
              variant={viewMode === 'grid' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setViewMode('grid')}
              className="rounded-r-none"
            >
              <LayoutGrid className="size-4" />
            </Button>
            <Button
              variant={viewMode === 'list' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setViewMode('list')}
              className="rounded-l-none"
            >
              <List className="size-4" />
            </Button>
          </div>
          
          <Button variant="outline" size="sm">
            <RefreshCw className="mr-2 size-4" />
            {t('common.refresh')}
          </Button>
          <Button onClick={onCreate}>
            <Plus className="mr-2 size-4" />
            {t('repositories.createRepository')}
          </Button>
        </div>
      </div>

      {/* Bulk Actions */}
      {selectedRepos.length > 0 && (
        <Card className="border-blue-200 dark:border-blue-800 bg-blue-50 dark:bg-blue-950/20">
          <CardContent className="py-3">
            <div className="flex items-center justify-between">
              <p className="text-sm text-gray-900 dark:text-white">
                {selectedRepos.length} {t('repositories.repositoriesSelected')}
              </p>
              <div className="flex items-center gap-2">
                <Button variant="outline" size="sm" onClick={() => setSelectedRepos([])}>
                  {t('repositories.clearSelection')}
                </Button>
                <Button variant="outline" size="sm" onClick={handleAnalyze}>
                  <Activity className="mr-2 size-4" />
                  {t('repositories.analyze')}
                </Button>
                <Button variant="outline" size="sm" onClick={handleAnalyze}>
                  <Shield className="mr-2 size-4" />
                  {t('repositories.scan')}
                </Button>
                <Button variant="destructive" size="sm" onClick={handleBulkDelete}>
                  <Trash2 className="mr-2 size-4" />
                  {t('common.delete')}
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Repository Grid View */}
      {viewMode === 'grid' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {paginatedRepositories.map((repo) => (
            <Card key={repo.id} className="hover:shadow-lg transition-all group relative cursor-pointer" onClick={() => onViewDetail(repo.id)}>
              {/* Selection Checkbox */}
              <div className="absolute top-4 left-4 z-10" onClick={(e) => e.stopPropagation()}>
                <Checkbox
                  checked={selectedRepos.includes(repo.id)}
                  onCheckedChange={() => handleToggleRepoSelection(repo.id)}
                  className="bg-white dark:bg-gray-800"
                />
              </div>

              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="flex-1 min-w-0 ml-8">
                    <div className="flex items-center gap-2 mb-2">
                      <Package className="size-5 text-gray-600 dark:text-gray-400 flex-shrink-0" />
                      <CardTitle className="truncate hover:text-blue-600 dark:hover:text-blue-400 transition-colors">{repo.name}</CardTitle>
                      {getStatusIcon(repo.status)}
                    </div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2">{repo.description}</p>
                  </div>
                  <div onClick={(e) => e.stopPropagation()}>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="sm">
                          <MoreVertical className="size-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end" className="w-48">
                        <DropdownMenuLabel>{t('repositories.actionsMenu')}</DropdownMenuLabel>
                        <DropdownMenuItem onClick={() => onEdit(repo.id)}>
                          <Settings className="mr-2 size-4" />
                          {t('repositories.configure')}
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={() => handleSecurityScan(repo)}>
                          <Shield className="mr-2 size-4" />
                          {t('repositories.securityScan')}
                        </DropdownMenuItem>
                        <DropdownMenuItem>
                          <Users className="mr-2 size-4" />
                          {t('repositories.manageAccess')}
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem onClick={() => handleCopyUrl(repo.url, repo.id)}>
                          <Copy className="mr-2 size-4" />
                          {t('repositories.copyUrl')}
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem 
                          className="text-red-600 dark:text-red-400"
                          onClick={() => handleDeleteRepository(repo)}
                        >
                          <Trash2 className="mr-2 size-4" />
                          {t('common.delete')}
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
                </div>
                <div className="flex items-center gap-2 mt-3 flex-wrap">
                  <Badge className={formatBadgeColor(repo.format)}>{repo.format}</Badge>
                  <Badge variant="outline" className={typeBadgeColor(repo.type)}>{repo.type}</Badge>
                  {repo.settings.public && (
                    <Badge variant="outline" className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                      <Globe className="mr-1 size-3" />
                      Public
                    </Badge>
                  )}
                  {repo.settings.indexed && (
                    <Badge variant="outline" className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300">
                      <Search className="mr-1 size-3" />
                      Indexed
                    </Badge>
                  )}
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* Health Score */}
                <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
                  <div className="flex items-center gap-2">
                    <Activity className="size-4 text-gray-600 dark:text-gray-400" />
                    <span className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.healthScoreLabel')}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className={`text-lg font-semibold ${getHealthColor(repo.health.score)}`}>
                      {repo.health.score}%
                    </span>
                    {repo.health.issues > 0 && (
                      <Badge variant="destructive" className="text-xs">
                        {repo.health.issues} {repo.health.issues === 1 ? t('repositories.issue') : t('repositories.issues')}
                      </Badge>
                    )}
                  </div>
                </div>

                {/* Stats Grid */}
                <div className="grid grid-cols-3 gap-3 text-sm">
                  <div className="text-center p-2 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
                    <p className="text-gray-600 dark:text-gray-400 text-xs mb-1">{t('repositories.artifacts')}</p>
                    <p className="text-gray-900 dark:text-white font-semibold">{repo.artifacts.toLocaleString()}</p>
                  </div>
                  <div className="text-center p-2 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
                    <p className="text-gray-600 dark:text-gray-400 text-xs mb-1">{t('common.size')}</p>
                    <p className="text-gray-900 dark:text-white font-semibold">{repo.size}</p>
                  </div>
                  <div className="text-center p-2 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
                    <p className="text-gray-600 dark:text-gray-400 text-xs mb-1">{t('repositories.downloads')}</p>
                    <p className="text-gray-900 dark:text-white font-semibold">
                      {repo.stats.downloads > 1000 
                        ? `${(repo.stats.downloads / 1000).toFixed(1)}k` 
                        : repo.stats.downloads}
                    </p>
                  </div>
                </div>

                {/* Security */}
                {repo.security.vulnerabilities > 0 && (
                  <div className="flex items-center gap-2 p-2 bg-red-50 dark:bg-red-950/20 border border-red-200 dark:border-red-900 rounded-lg">
                    <AlertTriangle className="size-4 text-red-600 dark:text-red-400 flex-shrink-0" />
                    <span className="text-sm text-red-700 dark:text-red-300">
                      {repo.security.vulnerabilities} {repo.security.vulnerabilities === 1 ? t('repositories.vulnerability') : t('repositories.vulnerabilities')} {t('repositories.vulnerabilitiesFound')}
                    </span>
                  </div>
                )}

                {/* Remote URL or Members */}
                {repo.remoteUrl && (
                  <div>
                    <p className="text-xs text-gray-600 dark:text-gray-400 mb-1">{t('repositories.remoteUrl')}</p>
                    <p className="text-xs font-mono text-blue-600 dark:text-blue-400 truncate bg-blue-50 dark:bg-blue-950/20 px-2 py-1 rounded">
                      {repo.remoteUrl}
                    </p>
                  </div>
                )}
                
                {repo.members && repo.members.length > 0 && (
                  <div>
                    <p className="text-xs text-gray-600 dark:text-gray-400 mb-2">{t('repositories.memberRepositories')}</p>
                    <div className="flex flex-wrap gap-1">
                      {repo.members.map(member => (
                        <Badge key={member} variant="secondary" className="text-xs">
                          {member}
                        </Badge>
                      ))}
                    </div>
                  </div>
                )}

                {/* Footer */}
                <div className="flex items-center justify-between pt-3 border-t border-gray-200 dark:border-gray-700">
                  <div className="flex items-center gap-1 text-xs text-gray-600 dark:text-gray-400">
                    <Clock className="size-3" />
                    {t('repositories.updated')} {repo.lastUpdated}
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleCopyUrl(repo.url, repo.id);
                    }}
                  >
                    {copiedUrl === repo.id ? (
                      <>
                        <Check className="mr-2 size-3" />
                        {t('artifacts.urlCopied')}
                      </>
                    ) : (
                      <>
                        <Copy className="mr-2 size-3" />
                        {t('repositories.copyUrl')}
                      </>
                    )}
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      {/* Repository List View */}
      {viewMode === 'list' && (
        <Card>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-12">
                  <Checkbox
                    checked={selectedRepos.length === paginatedRepositories.length && paginatedRepositories.length > 0}
                    onCheckedChange={handleSelectAll}
                  />
                </TableHead>
                <TableHead>{t('repositories.repository')}</TableHead>
                <TableHead>{t('repositories.type')}</TableHead>
                <TableHead>{t('repositories.format')}</TableHead>
                <TableHead>{t('repositories.health')}</TableHead>
                <TableHead>{t('repositories.artifacts')}</TableHead>
                <TableHead>{t('common.size')}</TableHead>
                <TableHead>{t('repositories.downloads')}</TableHead>
                <TableHead>{t('repositories.security')}</TableHead>
                <TableHead className="text-right">{t('repositories.actions')}</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {paginatedRepositories.map((repo) => (
                <TableRow key={repo.id} className="group cursor-pointer" onClick={() => onViewDetail(repo.id)}>
                  <TableCell onClick={(e) => e.stopPropagation()}>
                    <Checkbox
                      checked={selectedRepos.includes(repo.id)}
                      onCheckedChange={() => handleToggleRepoSelection(repo.id)}
                    />
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2">
                      <Package className="size-4 text-gray-600 dark:text-gray-400" />
                      <div>
                        <div className="font-medium text-gray-900 dark:text-white hover:text-blue-600 dark:hover:text-blue-400 transition-colors">{repo.name}</div>
                        <div className="text-xs text-gray-500 dark:text-gray-400 max-w-xs truncate">{repo.description}</div>
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline" className={typeBadgeColor(repo.type)}>{repo.type}</Badge>
                  </TableCell>
                  <TableCell>
                    <Badge className={formatBadgeColor(repo.format)}>{repo.format}</Badge>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2">
                      {getStatusIcon(repo.status)}
                      <span className={`font-medium ${getHealthColor(repo.health.score)}`}>
                        {repo.health.score}%
                      </span>
                    </div>
                  </TableCell>
                  <TableCell className="text-gray-900 dark:text-white">{repo.artifacts.toLocaleString()}</TableCell>
                  <TableCell className="text-gray-900 dark:text-white">{repo.size}</TableCell>
                  <TableCell className="text-gray-900 dark:text-white">
                    {repo.stats.downloads > 1000 
                      ? `${(repo.stats.downloads / 1000).toFixed(1)}k` 
                      : repo.stats.downloads}
                  </TableCell>
                  <TableCell>
                    {repo.security.vulnerabilities > 0 ? (
                      <Badge variant="destructive" className="text-xs">
                        {repo.security.vulnerabilities}
                      </Badge>
                    ) : (
                      <Badge variant="outline" className="text-xs bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                        <CheckCircle className="mr-1 size-3" />
                        {t('repositories.secure')}
                      </Badge>
                    )}
                  </TableCell>
                  <TableCell className="text-right" onClick={(e) => e.stopPropagation()}>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="sm">
                          <MoreVertical className="size-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end" className="w-48">
                        <DropdownMenuLabel>{t('repositories.actionsMenu')}</DropdownMenuLabel>
                        <DropdownMenuItem onClick={() => onEdit(repo.id)}>
                          <Settings className="mr-2 size-4" />
                          {t('repositories.configure')}
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={() => handleSecurityScan(repo)}>
                          <Shield className="mr-2 size-4" />
                          {t('repositories.securityScan')}
                        </DropdownMenuItem>
                        <DropdownMenuItem>
                          <Users className="mr-2 size-4" />
                          {t('repositories.manageAccess')}
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem onClick={() => handleCopyUrl(repo.url, repo.id)}>
                          <Copy className="mr-2 size-4" />
                          {t('repositories.copyUrl')}
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem 
                          className="text-red-600 dark:text-red-400"
                          onClick={() => handleDeleteRepository(repo)}
                        >
                          <Trash2 className="mr-2 size-4" />
                          {t('common.delete')}
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Card>
      )}

      {/* Pagination - Always show */}
      <div className="flex items-center justify-between border-t pt-4">
        <p className="text-sm text-gray-600 dark:text-gray-400">
          {filteredRepositories.length > 0 ? (
            <>
              {t('repositories.showing')} {startIndex + 1}-{Math.min(endIndex, filteredRepositories.length)} {t('repositories.ofTotal')} {filteredRepositories.length} {t('repositories.items')}
            </>
          ) : (
            t('repositories.noResults')
          )}
        </p>
        {totalPages > 1 && (
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
              disabled={currentPage === 1}
            >
              <ChevronLeft className="size-4" />
            </Button>
            <div className="flex items-center gap-1">
              {totalPages <= 7 ? (
                // Show all pages if 7 or less
                Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                  <Button
                    key={page}
                    variant={currentPage === page ? 'default' : 'outline'}
                    size="sm"
                    onClick={() => setCurrentPage(page)}
                    className="w-8 h-8 p-0"
                  >
                    {page}
                  </Button>
                ))
              ) : (
                // Show first, last, and pages around current
                <>
                  <Button
                    variant={currentPage === 1 ? 'default' : 'outline'}
                    size="sm"
                    onClick={() => setCurrentPage(1)}
                    className="w-8 h-8 p-0"
                  >
                    1
                  </Button>
                  {currentPage > 3 && <span className="px-1">...</span>}
                  {Array.from({ length: totalPages }, (_, i) => i + 1)
                    .filter(page => page > 1 && page < totalPages && Math.abs(page - currentPage) <= 1)
                    .map(page => (
                      <Button
                        key={page}
                        variant={currentPage === page ? 'default' : 'outline'}
                        size="sm"
                        onClick={() => setCurrentPage(page)}
                        className="w-8 h-8 p-0"
                      >
                        {page}
                      </Button>
                    ))}
                  {currentPage < totalPages - 2 && <span className="px-1">...</span>}
                  <Button
                    variant={currentPage === totalPages ? 'default' : 'outline'}
                    size="sm"
                    onClick={() => setCurrentPage(totalPages)}
                    className="w-8 h-8 p-0"
                  >
                    {totalPages}
                  </Button>
                </>
              )}
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
              disabled={currentPage === totalPages}
            >
              <ChevronRight className="size-4" />
            </Button>
          </div>
        )}
      </div>

      {/* Empty State */}
      {filteredRepositories.length === 0 && (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-16">
            <Package className="size-16 text-gray-400 mb-4" />
            <h3 className="text-xl text-gray-900 dark:text-white mb-2">{t('repositories.noRepositories')}</h3>
            <p className="text-gray-600 dark:text-gray-400 mb-6 text-center max-w-md">
              {searchQuery || filterType !== 'all' || filterFormat !== 'all' 
                ? t('repositories.noMatchingRepos')
                : t('repositories.noRepositoriesDesc')}
            </p>
            {!searchQuery && filterType === 'all' && filterFormat === 'all' && (
              <Button onClick={onCreate}>
                <Plus className="mr-2 size-4" />
                {t('repositories.createRepository')}
              </Button>
            )}
          </CardContent>
        </Card>
      )}

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>{t('repositories.deleteRepository')}</AlertDialogTitle>
            <AlertDialogDescription>
              {t('repositories.confirmDelete')}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>{t('common.cancel')}</AlertDialogCancel>
            <AlertDialogAction onClick={confirmDelete} className="bg-red-600 hover:bg-red-700">
              {t('common.delete')}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* Bulk Delete Confirmation Dialog */}
      <AlertDialog open={bulkDeleteDialogOpen} onOpenChange={setBulkDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>{t('repositories.bulkDelete')}</AlertDialogTitle>
            <AlertDialogDescription>
              {t('repositories.confirmBulkDelete')}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>{t('common.cancel')}</AlertDialogCancel>
            <AlertDialogAction onClick={confirmBulkDelete} className="bg-red-600 hover:bg-red-700">
              {t('common.delete')}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}